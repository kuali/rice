package org.springframework.cache.interceptor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;

/**
 * Methods for creating caching proxies.
 */
public final class CacheProxy {
    /*
        WARNING!  This class uses internal spring code and could be subject to breaking
        changes between Spring releases.

        This class is needed because Spring does not yet have a way to apply caching advise to
        existing objects which is required for our remote service proxies.

        Hopefully, Spring will provide this capability in the future and we can remove this class.
    */


    private CacheProxy() {
        throw new IllegalArgumentException("do not call");
    }

    /**
     * This creates a caching proxy around a existing object if the proxy can be applied.
     *
     * @param o the object to proxy.  cannot be null.
     * @param cacheManager the cacheManager to handle the caching operations.  cannot be null.
     * @return a proxy.  will not return null.
     * @throws IllegalArgumentException if the o or cacheManager is null
     */
    public static <T> T createCacheProxy(T o, CacheManager cacheManager) {
        if (o == null) {
            throw new IllegalArgumentException("o is null");
        }

        if (cacheManager == null) {
            throw new IllegalArgumentException("cacheManager is null");
        }

        //this method is following the logic found in AnnotationDrivenCacheBeanDefinitionParser.AopAutoProxyConfigurer

        final AnnotationCacheOperationSource source = new AnnotationCacheOperationSource();

        final CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheManager(cacheManager);
        interceptor.setCacheDefinitionSources(source);
        //important!  must call afterPropertiesSet or does not execute caching logic
        interceptor.afterPropertiesSet();

        final ProxyPointcut pointcut = new ProxyPointcut(source);
        final ProxyPointCutAdvisor advisor = new ProxyPointCutAdvisor(pointcut, interceptor);

        if (AopUtils.canApply(advisor, o.getClass())) {
            final ProxyFactory proxyFactory = new ProxyFactory(o);
            proxyFactory.addAdvisor(advisor);
            return (T) proxyFactory.getProxy();
        }
        return o;
    }

    private static class ProxyPointcut extends CacheOperationSourcePointcut {
        private static final long serialVersionUID = 6050508570006106939L;

        private final AnnotationCacheOperationSource source;

        private ProxyPointcut(AnnotationCacheOperationSource source) {
            this.source = source;
        }

        @Override
        protected CacheOperationSource getCacheOperationSource() {
            return source;
        }
    }

    private static class ProxyPointCutAdvisor extends AbstractPointcutAdvisor {

        private static final long serialVersionUID = 6050508570006106939L;

        private final CacheOperationSourcePointcut pointcut;
        private final CacheInterceptor interceptor;

        private ProxyPointCutAdvisor(CacheOperationSourcePointcut pointcut, CacheInterceptor interceptor) {
            this.pointcut = pointcut;
            this.interceptor = interceptor;
        }

        @Override
        public Pointcut getPointcut() {
            return pointcut;
        }

        @Override
        public Advice getAdvice() {
            return interceptor;
        }
    }
}
