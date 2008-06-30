#include <jni.h>
#include <libvirt/libvirt.h>
#include <string.h>
#include "VirConnectAuthCallbackBridge.h"
#include <assert.h>


int	VirConnectAuthCallbackBridge(virConnectCredentialPtr cred, unsigned int ncred, void * cbdata){

	//cbdata contains the java object that contains tha callback, as well as the JNI environment
	JNIEnv *env = ((CallBackStructType*)cbdata)->env;

	jobject j_auth = ((CallBackStructType*)cbdata)->auth;
	jclass j_auth_cls = (*env)->GetObjectClass(env, j_auth);
	jmethodID j_auth_cb_id=(*env)->GetMethodID(env, (*env)->GetObjectClass(env, j_auth), "callback", "([Lorg/libvirt/VirConnectCredential;)I");


	jclass j_cred_cls = (*env)->FindClass(env, "org/libvirt/VirConnectCredential");
	jmethodID j_cred_constructor = (*env)->GetMethodID(env, j_cred_cls, "<init>", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	jfieldID j_cred_result_id = (*env)->GetFieldID(env, j_cred_cls, "result", "Ljava/lang/String;");
	
	jobjectArray j_credArray = (*env)->NewObjectArray(env, ncred, j_cred_cls, NULL);
	
	//copy the credentials array to the Java object.
	int c;
	jobject j_cred;
	for(c=0; c<ncred; c++){
		j_cred=(*env)->NewObject(env, 
				j_cred_cls, 
				j_cred_constructor, 
				cred[c].type, 
				(*env)->NewStringUTF(env, cred[c].prompt),
				(*env)->NewStringUTF(env, cred[c].challenge),
				(*env)->NewStringUTF(env, cred[c].defresult));
		(*env)->SetObjectArrayElement(env, j_credArray, c, j_cred);
	}
	
	//Time to call the actual java callback function
	int retval = (*env)->CallNonvirtualIntMethod(env, 
			j_auth,
			j_auth_cls,
			j_auth_cb_id,
			j_credArray);

	if(retval){
		//The java callback function has failed, so we fail as well.
		return -1;
	}
	
	//If we are still here, the java callback returned sucessfully, so copy the results back.
	jstring j_cred_result;
	const char* result;
	
	for(c=0; c<ncred; c++){
		j_cred = (*env)->GetObjectArrayElement(env, j_credArray, c);
		j_cred_result = (*env)->GetObjectField(env, j_cred, j_cred_result_id);
		//If this assert triggers, then the user-supplied VirConnectAuth.callback function is broken
		assert(j_cred_result);
		result = (*env)->GetStringUTFChars(env, 
				j_cred_result,
				NULL);
		cred[c].result = strdup(result);
		cred[c].resultlen = strlen(result); 
		(*env)->ReleaseStringUTFChars(env, j_cred_result, result);
	}
	
	//All done, back to libvirt
	return 0;
	
}

