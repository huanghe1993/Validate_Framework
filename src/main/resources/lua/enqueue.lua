-- /lua/enqueue.lua
local zset_key = KEYS[1] -- ORDER_QUEUE
local hash_key = KEYS[2] -- ORDER_DETAIL_QUEUE
local zset_value = ARGV[1] --orderId
local zset_score = ARGV[2]  --currentTimeMillis
local hash_field = ARGV[3]  --OrderId
local hash_value = ARGV[4]  --detailMessage
redis.call('ZADD', zset_key, zset_score, zset_value)
redis.call('HSET', hash_key, hash_field, hash_value)
return nil