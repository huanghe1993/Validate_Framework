local zset_key = KEYS[1]  --ORDER_QUEUE
local hash_key = KEYS[2]  -- ORDER_DETAIL_QUEUE
local max_score = ARGV[1] -- max：分数区间 - 最大分数。
local min_score = ARGV[2]  -- min：分数区间 - 最小分数。
local offset = ARGV[3]  -- 偏移量offset
local limit = ARGV[4]  -- 个数count
-- TYPE命令的返回结果是{'ok':'zset'}这样子,这里利用next做一轮迭代
local status, type = next(redis.call('TYPE', zset_key))
if status ~= nil and status == 'ok' then
    if type == 'zset' then
        local list = redis.call('ZREVRANGEBYSCORE', zset_key, max_score, min_score, 'LIMIT', offset, limit)
        if list ~= nil and #list > 0 then
            -- unpack函数能把table转化为可变参数
            redis.call('ZREM', zset_key, unpack(list))
            local result = redis.call('HMGET', hash_key, unpack(list))
            redis.call('HDEL', hash_key, unpack(list))
            return result
        end
    end
end
return nil