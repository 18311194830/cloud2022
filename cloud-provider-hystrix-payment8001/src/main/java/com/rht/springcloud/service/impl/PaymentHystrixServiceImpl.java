package com.rht.springcloud.service.impl;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.rht.springcloud.service.PaymentHystrixService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.TimeUnit;

@Service
public class PaymentHystrixServiceImpl implements PaymentHystrixService {
    @Override
    public String Hystrix_OK(Integer id) {
        return "线程池" + Thread.currentThread().getName() + "Hystrix_OK,id:" + id;
    }

    @Override
    //规定该方法的正常限定时间为3秒
    @HystrixCommand(fallbackMethod = "Hystrix_TimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")
    })
    public String Hystrix_Timeout(Integer id) {
        //int age = 10/0;
        try {
            int timeNumber = 3;
            TimeUnit.SECONDS.sleep(timeNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池" + Thread.currentThread().getName() + "Hystrix_Timeout,id:" + id;
    }

    public String Hystrix_TimeoutHandler(Integer id){
        return "线程池" + Thread.currentThread().getName() + "Hystrix_Timeout,id:" + id+"\t"+"o(<||>)o";
    }

    //==============服务熔断
    @Override
    @HystrixCommand(fallbackMethod = "testRongDuan_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),  //是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),   //请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),  //时间范围
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60")}//失败率达到多少后跳闸
    )
    @RequestMapping("/test/hystrix/rongduan/{id}")
    public String paymentCircuitBreaker(@PathVariable Integer id){
        if (id < 0){
            // 这样做的目的是让他去执行兜底方法
            throw new RuntimeException("id 不能为负数");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName()+"\t"+"调用成功，流水号："+serialNumber;
    }

    public String testRongDuan_fallback(@PathVariable Integer id){

        return "id 不能为负数,降级服务执行…………  id:" + id;
    }


}
