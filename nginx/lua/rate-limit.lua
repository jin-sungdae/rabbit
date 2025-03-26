local redis = require "resty.redis"
local red = redis:new()
red:set_timeout(1000)

local ok, err = red:connect("redis", 6379)
if not ok then
  ngx.log(ngx.ERR, "[RATELIMIT] Redis connection failed: ", err)
  return ngx.exit(500)
end

if ngx.req.get_method() == "GET" then
  return
end

local key = "ratelimit:" .. ngx.req.get_method() .. ":" .. ngx.var.uri .. ":" .. ngx.var.binary_remote_addr
local limit = 5
local window = 10

local count, err = red:incr(key)
if not count then
  ngx.log(ngx.ERR, "[RATELIMIT] Redis INCR failed: ", err)
  return ngx.exit(500)
end
ngx.log(ngx.ERR, "[RATELIMIT] Redis INCR value: ", count)

local ttl, err = red:ttl(key)
ngx.log(ngx.ERR, "[RATELIMIT] TTL: ", ttl)
if ttl == -1 then
  local ok, expire_err = red:expire(key, window)
  if not ok then
    ngx.log(ngx.ERR, "[RATELIMIT] EXPIRE failed: ", expire_err)
    return ngx.exit(500)
  end
end

if count > limit then
  ngx.status = 429
  ngx.say("Rate limit exceeded for this URI.")
  return ngx.exit(429)
end

-- 반드시 연결 정리
red:set_keepalive(10000, 100)