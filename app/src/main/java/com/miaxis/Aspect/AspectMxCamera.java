package com.miaxis.Aspect;

import android.os.SystemClock;
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.zz.mr990Driver;

/**
 * @author Tank
 * @date 2021/6/15 12:23 上午
 * @des
 * @updateAuthor
 * @updateDes
 */
@Aspect
public class AspectMxCamera {

    final String TAG = AspectMxCamera.class.getSimpleName();

    @Before("execution(* *..MXCamera+.open(..))")
    public void before(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getThis().getClass().getSimpleName();
        Log.e(TAG, "before class:" + className);
        Log.e(TAG, "before method:" + methodSignature.getName());
        int zzCamControl = mr990Driver.zzCamControl(1);
        Log.e(TAG, "before zzCamControl:" + zzCamControl);
        SystemClock.sleep(600);
    }

    //    @Around("execution(* *..MainActivity+.test**(..))")
    //    public void around(JoinPoint joinPoint) throws Throwable {
    //        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    //        String className = joinPoint.getThis().getClass().getSimpleName();
    //
    //        Log.e(TAG, "around class:" + className);
    //        Log.e(TAG, "around method:" + methodSignature.getName());
    //
    ////        Object[] args = joinPoint.getArgs();
    ////
    ////        Log.e(TAG, "around args:" + Arrays.toString(args));
    //    }

    //    @After("execution(* *..MainActivity+.on**(..))")
    //    public void after(JoinPoint joinPoint) throws Throwable {
    //        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    //        String className = joinPoint.getThis().getClass().getSimpleName();
    //        Log.e(TAG, "After class:" + className);
    //        Log.e(TAG, "After method:" + methodSignature.getName());
    //    }

    //    @AfterReturning("execution(* *..MainActivity+.on**(..))")
    //    public void afterReturning(JoinPoint joinPoint) throws Throwable {
    //        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    //        String className = joinPoint.getThis().getClass().getSimpleName();
    //
    //
    //        Log.e(TAG, "AfterReturning class:" + className);
    //        Log.e(TAG, "AfterReturning method:" + methodSignature.getName());
    //
    ////        Class returnType = methodSignature.getReturnType();
    ////        Log.e(TAG, "AfterReturning returnType:" + returnType);
    //
    //
    //
    //    }
    //
    //    @AfterThrowing("execution(* *..MainActivity+.on**(..))")
    //    public void afterThrowing(JoinPoint joinPoint) throws Throwable {
    //        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    //        String className = joinPoint.getThis().getClass().getSimpleName();
    //
    //        Log.e(TAG, "AfterThrowing class:" + className);
    //        Log.e(TAG, "AfterThrowing method:" + methodSignature.getName());
    //    }

}
