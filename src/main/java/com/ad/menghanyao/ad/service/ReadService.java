package com.ad.menghanyao.ad.service;

import com.ad.menghanyao.ad.dto.SevenDayDTO;
import com.ad.menghanyao.ad.mapper.AdMapper;
import com.ad.menghanyao.ad.mapper.RecordMapper;
import com.ad.menghanyao.ad.model.Ad;
import com.ad.menghanyao.ad.model.Matrix;
import com.ad.menghanyao.ad.model.ReadModel;
import com.ad.menghanyao.ad.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ReadService {

    @Autowired
    private AdMapper adMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TimeService timeService;

    //四个区间，分别代表top、new、random、recommendation
    private static Integer [] rates = {0,20,50,75,100,120};

    // 每半小时自动更新热榜
    @Scheduled(cron = "0 30 * * * ?")
    public void setTop() {
        Integer size = 5;
        System.out.println("Service：setTop50，需要setTop50");
        List<Ad> adList = adMapper.getTop50(size);
        HashOperations<String,String,Ad> hashOperations = redisTemplate.opsForHash();
        for (int number = 1; number < size ; number++) {
            hashOperations.put("top", number + "", adList.get(number -1));
        }
        System.out.println("top1:"+ redisTemplate.opsForHash().get("top", 1+""));
    }

    //自动控制广告分发的比例
    @Scheduled(cron = "0 30 * * * ?")
    public void dynamicChangeRates() {
        SevenDayDTO sevenDayDTO = (SevenDayDTO) timeService.getSevenDayCount();
        Integer hours = (int) ((System.currentTimeMillis() - timeService.getTodayTimestamp()) / 3600000L);
        Long todayEven = sevenDayDTO.getAdCount()[0] / hours;
        Long yesterdayEven = sevenDayDTO.getAdCount()[1] / 24;
        if (todayEven >= yesterdayEven && rates[2] < 55) {
            rates[2] ++;
        } else if (todayEven < yesterdayEven && rates[2] >50) {
            rates[2] --;
        }
        Long thisHourEven = recordMapper.adCountByTime(System.currentTimeMillis() - 3600 * 1000);
        Long prevHourEven = recordMapper.adCountByTime(System.currentTimeMillis() - 7200 * 1000) - thisHourEven;
        if (thisHourEven >= prevHourEven && rates[3] > 65) {
            rates[3] --;
        } else if (thisHourEven < prevHourEven && rates[3] < 75) {
            rates[3] ++;
        }
    }

    // Controller调用分发策略
    public Object strategy(Long userId) {
        Integer random = (int)(Math.random() * 120 +1);
//        random = 20;
        if (random <= rates[1]) {
            // TOP,返回热榜
            ReadModel readModel = getTop();
            return readModel;
        } else if (random <= rates[2]) {
            // new,返回新广告
            ReadModel readModel = getNew();
            return readModel;
        } else if (random <= rates[3]) {
            // random，随机返回一个
            ReadModel readModel = getRandom();
            return readModel;
        } else if (random <= rates[4]) {
            // recommendation,返回算法推荐的
            ReadModel readModel = getRecommendation(userId);
            return readModel;
        } else {
            // types, 返回一个热门标签
            ReadModel readModel = getHotType();
            return  readModel;
        }
    }

    //从热门榜中随机推荐一个
    public ReadModel getTop() {
        Integer size = redisTemplate.opsForHash().keys("top").size();
        Integer random = (int) (Math.random() * size + 1);
        ReadModel readModel = new ReadModel();
        readModel.setSource("top");
        readModel.setAd(redisTemplate.opsForHash().get("top", random + ""));
        System.out.println("readModel = " + readModel);
        return readModel;
    }

    //从当日新增中随机推荐一个
    public ReadModel getNew() {
        Long past24Hours = System.currentTimeMillis() - 24 * 3600 * 1000;
        Long size = adMapper.adCountToday(past24Hours);
        Integer random = (int) (Math.random() * size + 1);
        Ad ad = adMapper.getNew(random, past24Hours);
        ReadModel readModel = new ReadModel();
        readModel.setSource("new");
        readModel.setAd(ad);
        return readModel;
    }

    //从数据库中随机推荐一个;
    public ReadModel getRandom() {
        Long size = adMapper.adCountAll();
        Integer random = (int) (Math.random() * size + 1);
        Ad ad = adMapper.getRandom(random);
        ReadModel readModel = new ReadModel();
        readModel.setSource("random");
        readModel.setAd(ad);
        return readModel;
    }

    //从推荐列表中选取一个
    public ReadModel getRecommendation(Long userId) {
        Integer size = redisTemplate.opsForHash().keys(userId + "").size();
        Integer random = (int) (Math.random() * size + 1);
        ReadModel readModel = new ReadModel();
        readModel.setSource("rec");
        readModel.setAd(redisTemplate.opsForHash().get(userId + "", random + ""));
        System.out.println("readModel = " + readModel);
        return readModel;
    }

    //从好评标签中选取一个
    public ReadModel getHotType() {
        Integer [] scores = {0,0,0,0,0};
        Integer size = redisTemplate.opsForHash().keys("top").size();
        for (int i = 1 ; i <= size ; i++) {
            Ad ad =  (Ad)redisTemplate.opsForHash().get("top", i + "");
            Integer adType = ad.getAdType();
            scores[adType] ++;
        }
        Integer recommendationType = 0;
        for (int i = 0 ; i < 5 ; i ++) {
            if (scores[i] >= recommendationType)
                recommendationType = scores[i];
        }
        Integer offsetRange = adMapper.getCountByAdType(recommendationType);
        Integer random = (int) (Math.random() * offsetRange + 1);
        ReadModel readModel = new ReadModel();
        readModel.setSource("type");
        Ad ad = adMapper.getAdByAdType(recommendationType, random);
        readModel.setAd(ad);
        System.out.println("readModel = " + readModel);
        return readModel;
    }


//    @Scheduled(cron = "0 0 2/6 * * ?")
//    public void training() {
//        String file = "src/data/testCF.csv";
//        DataModel model = new FileDataModel(new File(file));
//        UserSimilarity user = new EuclideanDistanceSimilarity(model);
//        NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, user, model);
//        Recommender r = new GenericUserBasedRecommender(model, neighbor, user);
//        LongPrimitiveIterator iter = model.getUserIDs();
//
//        HashOperations<String , String, Long> hashOperations = redisTemplate.opsForHash();
//
//        while (iter.hasNext()) {
//            long uid = iter.nextLong();
//            List<RecommendedItem> list = r.recommend(uid, RECOMMENDER_NUM);
//            System.out.printf("uid:%s", uid);
//            for (int i = 1; i<list.size(); i++) {
//                hashOperations.put(uid, i+"", list.get(i-1).getItemID());
//            }
////            for (RecommendedItem ritem : list) {
////                System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
////
////            }
//            System.out.println();
//        }
//    }

    @Scheduled(cron = "0 0 4/6 * * ?")
    public void training(Integer N, Integer K, Long time) {
        // N is recommendation numbers, K is neighbor numbers, time is start timestamp
        List<Record> originRecord = recordMapper.getRecordAfter(time);

        //first step: get pretreatment list
        List<Record> pretreatmentRecord = pretreatment(originRecord);

        //second step: get inversion list
        HashMap<Long ,List<Long>> inversion = getInversionList(pretreatmentRecord);

        //third step: get co-occurrence matrix
        Set<Long> userIdSet = new HashSet<>();
        for (Record record : originRecord) {
            userIdSet.add(record.getUserId());
        }
        List<Matrix> co_occurrenceMatrix = getCo_occurrenceMatrix(inversion, userIdSet, pretreatmentRecord);

        //fourth step: get distance
        List<Matrix> distanceMatrix = getDistance(co_occurrenceMatrix, userIdSet, pretreatmentRecord);

        //fifth step: select K neighbors
        HashMap<Long, List<Long>> neighbors = getNeighbors(distanceMatrix, userIdSet, K);

        //sixth step: recommend N ads for u
        recommend(userIdSet, neighbors, distanceMatrix, originRecord, N);
    }


    //first step: pretreatment function
    private List<Record> pretreatment(List<Record> originRecord) {
        Set<Long> adIdList = new HashSet<>();
        Set<Long> userIdList = new HashSet<>();
        List<Record> pretreatmentRecord = new ArrayList<>();
        for (Record record : originRecord) {
            adIdList.add(record.getAdId());
            userIdList.add(record.getUserId());
        }
        for (Long i : userIdList) {
            for (Long j : adIdList) {
                // generate or get a record
                Record element = originToElement(originRecord, i,j);
                pretreatmentRecord.add(element);
            }
        }
        return pretreatmentRecord;
    }

    // 查找原始列表的对应元素或者填充元素
    private Record originToElement(List<Record> originRecord, Long i, Long j) {
        for (Record record : originRecord) {
            if (record.getUserId() == i && record.getAdId() == j)
                return record;
        }
        Record result = new Record();
        result.setUserId(i);
        result.setAdId(j);
        result.setOperation(0);
        return result;
    }

    // second step: inversion List function
    private HashMap<Long, List<Long>> getInversionList(List<Record> pretreatmentRecord) {
        HashMap<Long ,List<Long>> inversion = new HashMap<>();
        Set<Long> adIdList = new HashSet<>();
        for (Record record : pretreatmentRecord) {
            adIdList.add(record.getAdId());
        }
        for (Long item : adIdList) {    //扫描每个广告
            List<Long> userIdList = new ArrayList<>();
            for (Record record: pretreatmentRecord) { //针对观看该广告的每个用户
                if (record.getAdId() == item && record.getOperation() != 0) { //如果评分>0，就把相应的次数放进list
                    for (int i = 0 ; i < record.getOperation() ; i ++) {
                        userIdList.add(record.getUserId());
                    }
                }
            }
            inversion.put(item, userIdList);
        }
        return inversion;
    }

    //third step: co-occurrence matrix function
    private List<Matrix> getCo_occurrenceMatrix(HashMap<Long, List<Long>> inversion, Set<Long> userIdSet, List<Record> pretreatmentRecord) {
        List<Matrix> matrices = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>(userIdSet);
        // 初始化共现矩阵，把所有user距离置为0
        for (int i = 0 ; i < userIdSet.size() ; i ++) {
            for (int j = 0 ; j < userIdSet.size() ; j ++) {
                Matrix element = new Matrix();
                element.setRow(userIdList.get(i));
                element.setColumn(userIdList.get(j));
                element.setOperation(0.0);
                matrices.add(element);
            }
        }
        for (Matrix item : matrices) {
            if (item.getRow() != item.getColumn()) {
                Double operation = 0.0;
                for (Map.Entry<Long, List<Long>> entry: inversion.entrySet()) {
                    if (entry.getValue().contains(item.getRow()) && entry.getValue().contains(item.getColumn())
                            && getNumber(entry.getValue(), item.getRow()) * getNumber(entry.getValue(), item.getColumn()) >= 6) {
                        Double res1 = 0.0;
                        if (redisTemplate.opsForHash().entries("top").containsKey(entry.getKey())) {
                            // 需要惩罚热门列表中广告的权重
                            res1 = 1 / Math.log(1 + getNumber(entry.getValue(), item.getRow()) * getNumber(entry.getValue(), item.getColumn()));
                        } else  {
                            res1 = getNumber(entry.getValue(), item.getRow()) * getNumber(entry.getValue(), item.getColumn()) * 1.0;
                        }
                       operation += res1;
                    }
                }
                item.setOperation(operation);
            }
        }
        return matrices;
    }

    //  统计每个元素在list中出现的次数
    private Integer getNumber(List<Long> list, Long element) {
        Integer count = 0;
        for (Long item : list) {
            if (element == item)
                count++;
        }
        return count;
    }

    //fourth step: distance function
    private List<Matrix> getDistance(List<Matrix> co_occurrenceMatrix, Set<Long> userIdSet, List<Record> pretreatmentRecord) {
        List<Long> userIdList = new ArrayList<>(userIdSet);
        HashMap<Long, Double> moldLength = new HashMap<>();
        // 计算每个user的模长
        for (int i = 0 ; i < pretreatmentRecord.size() ; i ++) {
            Double mold = 0.0;
            for (Record record : pretreatmentRecord) {
                if (userIdList.get(i) == record.getUserId()) {
                    mold += Math.pow(record.getOperation(), 2);
                }
            }
            moldLength.put(userIdList.get(i), Math.sqrt(mold));
        }
        for (Matrix item : co_occurrenceMatrix) {
            item.setOperation(item.getOperation() / (moldLength.get(item.getRow() * moldLength.get(item.getColumn()))));
        }
        return co_occurrenceMatrix;
    }

    //fifth step: get K neighbors function
    private HashMap<Long, List<Long>> getNeighbors(List<Matrix> distanceMatrix, Set<Long> userIdSet, Integer k) {
        List<Long> userIdList = new ArrayList<>(userIdSet);
        HashMap<Long, List<Long>> neighbors = new HashMap<>();
        for (Long userId : userIdList) {
            List<Matrix> orderList = new ArrayList<>();
            for (Matrix item : distanceMatrix) {
                if (userId == item.getRow())
                    orderList.add(item);
            }
            Collections.sort(orderList);
            List<Long> kNeighbors = new ArrayList<>();
            for (int i = 0 ; i < k ; i ++) {
                kNeighbors.add(orderList.get(i).getColumn());
            }
            neighbors.put(userId, kNeighbors);
        }
        return neighbors;
    }

    //sixth: recommend N ad for user in para1 with K neighbors' record
    private void recommend(Set<Long> userIdSet, HashMap<Long, List<Long>> neighbors, List<Matrix> distanceMatrix, List<Record> originRecord, Integer N) {
        HashSet<Long> adIdSet = new HashSet<>();
        for (Record record : originRecord) {
            adIdSet.add(record.getAdId());
        }
        for (Long userId : userIdSet) {
            List<Long> kNeighbors = neighbors.get(userId);
            HashMap<Long, Double> scoreMap = new HashMap<>(); //存放广告评价
            for (Long adId : adIdSet) {
                Double totalScore = 0.0;
                for (Long neighbor: kNeighbors) {
                    Double rate = 0.0;
                    Double singleScore = 0.0;
                    for (Matrix matrix: distanceMatrix) {
                        rate = matrix.getOperation(userId, neighbor);
                    }
                    for (Record record: originRecord) {
                        singleScore =(double) record.getOperation(neighbor, adId);
                    }
                    totalScore += rate * singleScore;
                }
                scoreMap.put(adId, totalScore);
            }
            List<HashMap.Entry<Long, Double>> scoreList = new ArrayList<>(scoreMap.entrySet());
            Collections.sort(scoreList, new Comparator<HashMap.Entry<Long, Double>>() {
                @Override
                public int compare(Map.Entry<Long, Double> entry1, Map.Entry<Long, Double> entry2) {
                    return (int) (entry1.getValue() - entry2.getValue());
                }
            });
            for (int i = 0 ; i < N ; i++) {
                redisTemplate.opsForHash().put(userId + "", i + 1 + "", scoreList.get(i).getKey());
            }
        }
    }
}


