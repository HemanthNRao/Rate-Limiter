package com.tuebora.assignment.ratelimiter;

import java.util.function.Function;

/**
 * Interface for Rate Limiter
 * @param <Input>
 * @param <Output>
 */
public interface RateLimiter<Input, Output>
{
    Function<Input, Output> wrap(Function<Input, Output> function);
}
