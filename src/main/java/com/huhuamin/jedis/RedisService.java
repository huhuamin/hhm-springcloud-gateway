package com.huhuamin.jedis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: Huhuamin
 * @Date: 2020/3/14 20:28
 * @Description:
 */
@Component
public class RedisService {
    /*防止公用一个系统,数据库备注*/
    @Value("${spring.redis.sysName}")
    private String DB_PREFIX;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 默认7天
     */
    private final int EXPIRE_SECOND = 60 * 60 * 24 * 7;

    /**
     * 锁，默认等待时间7s，锁25
     *
     * @param lockName
     * @return
     */
    public String acquireLock(String lockName) {
        return acquireLock(lockName, 4000, 25000);
    }

    /**
     * @param lockName       锁名称
     * @param acquierTimeout 获得或超时时间 毫秒
     * @param lockTimeout    锁的过期时间  毫秒
     * @return
     */
    public String acquireLock(String lockName, long acquierTimeout, long lockTimeout) {

        String result = null;
        try {
            String identifier = UUID.randomUUID().toString();//释放锁的时候持有者校验
            String key = DB_PREFIX + "lock:" + lockName;


            int lockExpire = (int) lockTimeout / 1000;//锁秒数
            long end = System.currentTimeMillis() + acquierTimeout;
            while (System.currentTimeMillis() < end) {//阻塞
//                if (jedis.setnx(key, identifier) == 1) {//设置超时时间
                if (redisTemplate.opsForValue().setIfAbsent(key, identifier)) {
//                    jedis.expire(key, lockExpire);
                    redisTemplate.expire(key, lockExpire, TimeUnit.SECONDS);
                    //锁设置成功，redis操作成功
                    result = identifier;
                    break;
                }

//                if (jedis.ttl(key) == -1) { //检测过期时间
                if (redisTemplate.getExpire(key) == -1) {
                    redisTemplate.expire(key, lockExpire, TimeUnit.SECONDS);
                }
                Thread.sleep(300);//睡眠时间
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 释放锁
     *
     * @param lockName
     * @param identifier
     * @return
     */
    public boolean releaseLock(String lockName, String identifier) {

        Boolean isRelease = false;
        try {
            String key = DB_PREFIX + "lock:" + lockName;

            while (true) {
//                jedis.watch(key);  //watch
                redisTemplate.watch(key);
//                if (identifier.equals(jedis.get(key))) { //判断获得锁的线程和当前redis中存的锁是同一个
                if (identifier.equals(redisTemplate.opsForValue().get(key))) { //判断获得锁的线程和当前redis中存的锁是同一个
//                    Transaction transaction = jedis.multi();
                    redisTemplate.multi();
//                    transaction.del(key);
                    redisTemplate.delete(key);
//                    List<Object> list = transaction.exec();
                    List<Object> list = redisTemplate.exec();
                    if (list == null || list.isEmpty()) {
                        continue;
                    }
                    isRelease = true;
                } else {
                    isRelease = false;
                }
                redisTemplate.unwatch();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRelease;
    }


    public void setKeyValue(String key, String value, Integer sec) {
//        Jedis jedis = null;
        try {
            key = DB_PREFIX + key;
//            jedis = jedisPool.getResource();
            if (null == sec) {
//                jedis.setex(key, EXPIRE_SECOND, value);
                redisTemplate.opsForValue().set(key, value, EXPIRE_SECOND, TimeUnit.SECONDS);
            } else {
//                jedis.setex(key, sec, value);
                redisTemplate.opsForValue().set(key, value, sec, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String getCustIdByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return "";
        }
//        Jedis jedis = null;
        try {
            String key = token;
            key = DB_PREFIX + key;
//            jedis = jedisPool.getResource();
//            String curCustId = jedis.get(key);
            String curCustId = redisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(curCustId)) {
                return "";
            }
//            String curToken = jedis.get(DB_PREFIX + curCustId);
            String curToken = redisTemplate.opsForValue().get(DB_PREFIX + curCustId);
            if (StringUtils.isEmpty(curToken)||!curToken.equalsIgnoreCase(token)) {
                return "303";
            } else {
                return curCustId;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            jedis.close();
//        }
        return "";
    }

    public String getValueByKey(String key) {
//        Jedis jedis = null;
        try {
            key = DB_PREFIX + key;
//            jedis = jedisPool.getResource();
            return redisTemplate.opsForValue().get(key);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            jedis.close();
//        }
        return "";
    }

    public void delByKey(String key) {
//        Jedis jedis = null;
        try {
            key = DB_PREFIX + key;
//            jedis = jedisPool.getResource();
//            jedis.del(key);
            redisTemplate.delete(key);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            jedis.close();
//        }

    }

    public void refreshCustToken(String custId, String token) {

//        SocketJson socketJson = new SocketJson();
//        socketJson.setCustType(3);
//        socketJson.setFormType("otherLogin");
//        socketJson.setType("其他设备登录");
//        WebSocket.sendMessageCustId(custId, JSON.toJSONString(socketJson));
        setKeyValue(token, custId, null);
        setKeyValue(custId, token, null);

    }

    /**
     * 今天24点后过期
     *
     * @param key
     * @param value
     */
    public void setKeyValueToDay(String key, String value) {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(now);
        Long dif = calendar.getTimeInMillis() - calendar2.getTimeInMillis();
        int sec = (int) (dif / 1000);
        setKeyValue(key, value, sec);
    }
}
