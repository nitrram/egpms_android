#pragma once
#include <jni.h>
#include <android/log.h>

#define ON_LOAD(vm, c_name, t_m) JNIEnv *env;				\
  if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {	\
    return JNI_ERR;							\
  } else {								\
    jclass clazz = env->FindClass(c_name);			\
    if (clazz) {							\
      int t_m_siz = sizeof(t_m) / sizeof(t_m[0]);			\
      jint ret = env->RegisterNatives(clazz, t_m, t_m_siz);	\
      env->DeleteLocalRef(clazz);				\
      return ret == 0 ? JNI_VERSION_1_6 : JNI_ERR;			\
    } else return JNI_ERR;						\
  }


#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

