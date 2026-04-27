package com.ran.commons.data.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ran.commons.data.UserInfoDataService;
import com.ran.commons.entity.UserInfo;
import com.ran.commons.mapper.UserInfoMapper;
import com.ran.commons.util.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserInfoDataServiceImpl implements UserInfoDataService {
    private static final Random RANDOM = new Random();

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserInfoMapper userInfoMapper;


    private static final String[] CHINESE_ADJECTIVES = {
            "快乐的", "聪明的", "勇敢的", "温柔的", "活泼的", "阳光的", "可爱的", "优雅的",
            "神秘的", "幸运的", "开朗的", "善良的", "机智的", "热情的", "淡定的", "灵动的"
    };

    private static final String[] CHINESE_NOUNS = {
            "小猫", "小狗", "小鸟", "小鱼", "熊猫", "兔子", "狐狸", "松鼠",
            "星星", "月亮", "云朵", "花朵", "树叶", "彩虹", "蝴蝶", "小熊"
    };

    private static final String[] ENGLISH_ADJECTIVES = {
            "Happy", "Smart", "Brave", "Gentle", "Lively", "Sunny", "Cute", "Elegant",
            "Mysterious", "Lucky", "Cheerful", "Kind", "Clever", "Warm", "Cool", "Swift"
    };

    private static final String[] ENGLISH_NOUNS = {
            "Cat", "Dog", "Bird", "Fish", "Panda", "Rabbit", "Fox", "Squirrel",
            "Star", "Moon", "Cloud", "Flower", "Leaf", "Rainbow", "Butterfly", "Bear"
    };
    @Override
    public Optional<UserInfo> findByUid(String uid) {
        if (uid == null) {
            return Optional.empty();
        }
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUid, uid)
                .last("LIMIT 1");
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);
        return Optional.ofNullable(userInfo);
    }

    @Override
    public UserInfo createOrGetUser(UserInfo userInfo) {
        if (userInfo == null) {
            throw new IllegalArgumentException("User information cannot be null");
        }

        if (userInfo.getUid() == null) {
            throw new IllegalArgumentException("User UID cannot be null");
        }
        Optional<UserInfo> existingUser = findByUid(userInfo.getUid());
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        String lockKey = "user:create:uid:" + userInfo.getUid();
        //加上分布式锁，创建过程只能有一个进程
        RLock lock = redissonClient.getLock(lockKey);

        try{
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("Timed out acquiring distributed lock, please try again later");
            }
            try{
                Optional<UserInfo> existingUserInLock = findByUid(userInfo.getUid());
                if (existingUserInLock.isPresent()) {
                    return existingUserInLock.get();
                }
                // Set default values
                LocalDateTime now = LocalDateTime.now();
                if (userInfo.getCreateTime() == null) {
                    userInfo.setCreateTime(now);
                }
                if (userInfo.getUpdateTime() == null) {
                    userInfo.setUpdateTime(now);
                }
                if (userInfo.getDeleted() == null) {
                    userInfo.setDeleted(0);
                }
                if (StringUtils.isBlank(userInfo.getNickname())) {
                    userInfo.setNickname(generateRandomNickname());
                }
                userInfo.setId(null);
                userInfoMapper.insert(userInfo);
                log.info("Created new user: uid={}, username={}", userInfo.getUid(), userInfo.getUsername());
                return userInfo;

            } finally {
                // Release the lock
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while acquiring distributed lock", e);
        }
    }
    private String generateRandomNickname(){
        String language = I18nUtil.getLanguage();
        if ("zh".equals(language)) {
            String adjective = CHINESE_ADJECTIVES[RANDOM.nextInt(CHINESE_ADJECTIVES.length)];
            String noun = CHINESE_NOUNS[RANDOM.nextInt(CHINESE_NOUNS.length)];
            int number = RANDOM.nextInt(1000);
            return adjective + noun + number;
        } else {
            String adjective = ENGLISH_ADJECTIVES[RANDOM.nextInt(ENGLISH_ADJECTIVES.length)];
            String noun = ENGLISH_NOUNS[RANDOM.nextInt(ENGLISH_NOUNS.length)];
            int number = RANDOM.nextInt(1000);
            return adjective + noun + number;
        }
    }
}
