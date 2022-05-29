package com.rht.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.rht.springcloud.service.PaymentHystrixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod")
public class OrderHystrixController {

    @Resource
    private PaymentHystrixService paymentHystrixService;

    @GetMapping("/consumer/payment/hystrix/ok/{id}")
    public String Hystrix_OK(@PathVariable("id") Integer id){
        String result = paymentHystrixService.Hystrix_OK(id);
        return result;
    }

    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    /*@HystrixCommand(fallbackMethod = "Hystrix_TimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1500")
    })*/
    @HystrixCommand
    public String Hystrix_Timeout(@PathVariable("id") Integer id){
        String result = paymentHystrixService.Hystrix_Timeout(id);
        return result;
    }

    public String Hystrix_TimeoutHandler(Integer id){
        return "线程池" + Thread.currentThread().getName() + "消费者80，支付系统繁忙，请10秒钟之后再试！！！";
    }

    //全局fallback方法
    public String payment_Global_FallbackMethod(){
        return "Global异常处理信息，请稍后再试！！！";
    }
}
