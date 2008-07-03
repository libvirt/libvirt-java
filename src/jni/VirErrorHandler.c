#include "VirErrorHandler.h"
#include <stdio.h>
#include <libvirt/virterror.h>
#include <jni.h>

void virErrorHandler(void *userdata, virErrorPtr error){
	JNIEnv *env=userdata;
	jclass exception_cls= (*env)->FindClass(env, "org/libvirt/LibvirtException");
	jclass error_cls= (*env)->FindClass(env, "org/libvirt/VirError");
	jobject j_error = (*env)->NewObject(env, error_cls, (*env)->GetMethodID(env, error_cls, "<init>", "()V"));
	jobject j_exception;
	
	//Get objects for the errorNumber
	jclass number_class=(*env)->FindClass(env,"Lorg/libvirt/VirError$VirErrorNumber;");
	jmethodID number_values_id=(*env)->GetStaticMethodID(env, number_class, "values", "()[Lorg/libvirt/VirError$VirErrorNumber;");
	jarray number_values=(*env)->CallStaticObjectMethod(env, number_class, number_values_id);
	
	//Ditto for errorLevel
	jclass level_class=(*env)->FindClass(env,"Lorg/libvirt/VirError$VirErrorLevel;");
	jmethodID level_values_id=(*env)->GetStaticMethodID(env, level_class, "values", "()[Lorg/libvirt/VirError$VirErrorLevel;");
	jarray level_values=(*env)->CallStaticObjectMethod(env, level_class, level_values_id);
	
	//Ditto for errorDomain
	jclass domain_class=(*env)->FindClass(env,"Lorg/libvirt/VirError$VirErrorDomain;");
	jmethodID domain_values_id=(*env)->GetStaticMethodID(env, domain_class, "values", "()[Lorg/libvirt/VirError$VirErrorDomain;");
	jarray domain_values=(*env)->CallStaticObjectMethod(env, domain_class, domain_values_id);
	
	//Straight copy everything 
	(*env)->SetObjectField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "code", "Lorg/libvirt/VirError$VirErrorNumber;"),
			(*env)->GetObjectArrayElement(env, number_values, error->code));
	(*env)->SetObjectField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "domain", "Lorg/libvirt/VirError$VirErrorDomain;"),
			(*env)->GetObjectArrayElement(env, domain_values, error->domain));
	(*env)->SetObjectField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "level", "Lorg/libvirt/VirError$VirErrorLevel;"),
			(*env)->GetObjectArrayElement(env, level_values, error->level));
	(*env)->SetObjectField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "message", "Ljava/lang/String;"),
			(*env)->NewStringUTF(env, error->message));
#if 0
/*
 * use of those fields got deprecated
 */
	(*env)->SetLongField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "VCP", "J"),
			(long)error->conn);
	(*env)->SetLongField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "VDP", "J"),
			(long)error->dom);
	(*env)->SetLongField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "VNP", "J"),
			(long)error->net);
#else
	(*env)->SetLongField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "VCP", "J"),
			(long)0);
	(*env)->SetLongField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "VDP", "J"),
			(long)0);
	(*env)->SetLongField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "VNP", "J"),
			(long)0);
#endif
	(*env)->SetObjectField(
				env, 
				j_error, 
				(*env)->GetFieldID(env, error_cls, "str1", "Ljava/lang/String;"),
				(*env)->NewStringUTF(env, error->str1));		
	(*env)->SetObjectField(
				env, 
				j_error, 
				(*env)->GetFieldID(env, error_cls, "str2", "Ljava/lang/String;"),
				(*env)->NewStringUTF(env, error->str2));		
	(*env)->SetObjectField(
				env, 
				j_error, 
				(*env)->GetFieldID(env, error_cls, "str3", "Ljava/lang/String;"),
				(*env)->NewStringUTF(env, error->str3));
	(*env)->SetIntField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "int1", "I"),
			error->int1);
	(*env)->SetIntField(
			env, 
			j_error, 
			(*env)->GetFieldID(env, error_cls, "int2", "I"),
			error->int2);
	
	//Now we have the VirError object properly initialized
	j_exception = (*env)->NewObject(env, exception_cls, 
			(*env)->GetMethodID(env, exception_cls, "<init>", "(Lorg/libvirt/VirError;)V"),
			j_error);		
	
	(*env)->ExceptionDescribe(env);
	
	(*env)->Throw(env, j_exception);
}
