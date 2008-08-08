#include "org_libvirt_Connect.h"
#include <libvirt/libvirt.h>
#include <stdlib.h>
#include "ErrorHandler.h"
#include "ConnectAuthCallbackBridge.h"
#include "generic.h"
#include <assert.h>

//TODO leak check for *ArrayElements

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1virInitialize
  (JNIEnv *env, jclass cls){
	int result;
	result=virInitialize();
	//The connection-less errors go to the initializing thread as an exception.
	//Not ideal, but better than just dropping the errors.
	virSetErrorFunc(env, virErrorHandler);
	return result;
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1close
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__INT(env, obj, (virConnectPtr)VCP, virConnectClose)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Connect__1getHostName
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__STRING(env, obj, (virConnectPtr)VCP, virConnectGetHostname)
};

JNIEXPORT jstring JNICALL Java_org_libvirt_Connect__1getCapabilities
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__STRING(env, obj, (virConnectPtr)VCP, virConnectGetCapabilities)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1getMaxVcpus
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_type){
	const char *type = (*env)->GetStringUTFChars(env, j_type, NULL);
	int retval = (jint)virConnectGetMaxVcpus((virConnectPtr)VCP, type);
	(*env)->ReleaseStringUTFChars(env, j_type, type);
	return retval;
};

JNIEXPORT jstring JNICALL Java_org_libvirt_Connect__1getType
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__CONSTSTRING(env, obj, (virConnectPtr)VCP, virConnectGetType)
};

JNIEXPORT jstring JNICALL Java_org_libvirt_Connect__1getURI
  (JNIEnv *env, jobject obj, jlong VCP){
	jstring j_uri=NULL;
	char *uri;
	if((uri = virConnectGetURI((virConnectPtr)VCP))){
		j_uri = (*env)->NewStringUTF(env, uri);
		free(uri);
	}
	return j_uri;
};

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1getVersion
  (JNIEnv *env, jobject obj, jlong VCP){
	unsigned long hvVer=0;
	virConnectGetVersion((virConnectPtr)VCP, &hvVer);
	return (jlong)(hvVer);
};

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Connect__1listDefinedNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC_LIST_STRINGARRAY(env, obj, (virConnectPtr)VCP, virConnectListDefinedNetworks, virConnectNumOfDefinedNetworks)
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Connect__1listNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC_LIST_STRINGARRAY(env, obj, (virConnectPtr)VCP, virConnectListNetworks, virConnectNumOfNetworks)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfDefinedDomains
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__INT(env, obj, (virConnectPtr)VCP, virConnectNumOfDefinedDomains)
};

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfDefinedNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__INT(env, obj, (virConnectPtr)VCP, virConnectNumOfDefinedNetworks)
};

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfDomains
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__INT(env, obj, (virConnectPtr)VCP, virConnectNumOfDomains)
};

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__INT(env, obj, (virConnectPtr)VCP, virConnectNumOfNetworks)
};

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1open
  (JNIEnv *env, jobject obj, jstring j_uri){

	virConnectPtr vc;
	const char *uri=(*env)->GetStringUTFChars(env, j_uri, NULL);

	//Initialize the libvirt VirtConn Object
	vc=virConnectOpen(uri);
	(*env)->ReleaseStringUTFChars(env, j_uri, uri);
	if(vc==NULL){
		//We have a pending java exception, let's return
		assert((*env)->ExceptionOccurred(env));
		return (jlong)NULL;
	}

	//Initialize the error handler for this connection
	virConnSetErrorFunc(vc, env, virErrorHandler);

	return (jlong)vc;
};

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1openReadOnly
  (JNIEnv *env, jobject obj, jstring j_uri){

	virConnectPtr vc;
	const char *uri=(*env)->GetStringUTFChars(env, j_uri, NULL);

	//Initialize the libvirt VirtConn Object
	vc=virConnectOpenReadOnly(uri);
	(*env)->ReleaseStringUTFChars(env, j_uri, uri);
	if(vc==NULL){
		//We have a pending java exception, let's return
		assert((*env)->ExceptionOccurred(env));
		return (jlong)NULL;
	}

	//Initialized the error handler for this connection
	virConnSetErrorFunc(vc, env, virErrorHandler);

	return (jlong)vc;
};

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1openAuth
  (JNIEnv *env, jobject obj, jstring j_uri, jobject j_auth, jint flags){

	virConnectPtr vc;
	const char *uri=(*env)->GetStringUTFChars(env, j_uri, NULL);

	virConnectAuth *auth = malloc(sizeof(virConnectAuth));

	jobject j_credTypeElement;
	int c;

	//Prepare by computing the class and field IDs
	jfieldID credTypeArray_id = (*env)->GetFieldID(env,
			(*env)->FindClass(env, "org/libvirt/ConnectAuth"),
			"credType",
			"[Lorg/libvirt/ConnectAuth$CredentialType;");
	jmethodID credTypeMapToInt_id = (*env)->GetMethodID(env,
			(*env)->FindClass(env, "org/libvirt/ConnectAuth$CredentialType"),
			"mapToInt",
			"()I");

	//Copy the array of credtypes with the helper function
	jarray j_credTypeArray=(*env)->GetObjectField(env, j_auth, credTypeArray_id);
	auth->ncredtype = (*env)->GetArrayLength(env, j_credTypeArray);

	auth->credtype = calloc(auth->ncredtype, sizeof(int));
	for(c=0; c< auth->ncredtype; c++){
		j_credTypeElement = (*env)->GetObjectArrayElement(env, j_credTypeArray, c);
		auth->credtype[c]=(*env)->CallIntMethod(env, j_credTypeElement, credTypeMapToInt_id);
	}

	//The callback function is always ConnectAuthCallbackBridge
	auth->cb = &ConnectAuthCallbackBridge;
	//We pass the ConnectAuth object and the JNI env in cdbata
	CallBackStructType* cb_wrapper;
	cb_wrapper = malloc(sizeof(CallBackStructType));
	cb_wrapper->env = env;
	cb_wrapper->auth = j_auth;
	auth->cbdata=cb_wrapper;

	vc=virConnectOpenAuth(uri, auth, flags);
	(*env)->ReleaseStringUTFChars(env, j_uri, uri);
	if (vc==NULL){
		//We have a pending java exception, let's return
		assert((*env)->ExceptionOccurred(env));
		return (jlong)NULL;
	}

	//Initialize the error handler for this connection
	virConnSetErrorFunc(vc, env, virErrorHandler);

	return (jlong)vc;
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkCreateXML
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_xmlDesc){
	GENERIC_VIROBJ_STRING__VIROBJ(env, obj, (virConnectPtr)VCP, j_xmlDesc, virNetworkCreateXML)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkDefineXML
(JNIEnv *env, jobject obj, jlong VCP, jstring j_xmlDesc){
	GENERIC_VIROBJ_STRING__VIROBJ(env, obj, (virConnectPtr)VCP, j_xmlDesc, virNetworkDefineXML)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkLookupByName
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_name){
	GENERIC_LOOKUPBY_STRING(env, obj, (virConnectPtr)VCP, j_name, virNetworkLookupByName)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkLookupByUUID
  (JNIEnv *env, jobject obj, jlong VCP, jintArray j_UUID){
	GENERIC_LOOKUPBY_UUID(env, obj, (virConnectPtr)VCP, j_UUID, virNetworkLookupByUUID)
}


JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkLookupByUUIDString
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_UUID){
	GENERIC_LOOKUPBY_STRING(env, obj, (virConnectPtr)VCP, j_UUID, virNetworkLookupByUUIDString)
}

JNIEXPORT jobject JNICALL Java_org_libvirt_Connect__1virNodeInfo
  (JNIEnv *env, jobject obj, jlong VCP){
	virNodeInfo nodeInfo;
	jobject j_nodeInfo;
	jclass cls = (*env)->FindClass(env, "org/libvirt/NodeInfo");

	//Fill the c struct
	if(virNodeGetInfo((virConnectPtr)VCP, &nodeInfo)<0){
		return NULL;
	}

	//Allocate the VirInfo Object
	j_nodeInfo = (jobject)(*env)->AllocObject(env, cls);

	//Copy the fields
	(*env)->SetObjectField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "model", "Ljava/lang/String;"), (*env)->NewStringUTF(env, nodeInfo.model));
	(*env)->SetLongField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "memory", "J"), nodeInfo.memory);
	(*env)->SetIntField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "cpus", "I"), nodeInfo.cpus);
	(*env)->SetIntField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "mhz", "I"), nodeInfo.mhz);
	(*env)->SetIntField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "cpus", "I"), nodeInfo.cpus);
	(*env)->SetIntField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "nodes", "I"), nodeInfo.nodes);
	(*env)->SetIntField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "sockets", "I"), nodeInfo.sockets);
	(*env)->SetIntField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "cores", "I"), nodeInfo.cores);
	(*env)->SetIntField(env, j_nodeInfo, (*env)->GetFieldID(env, cls, "threads", "I"), nodeInfo.threads);

	return j_nodeInfo;
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Connect__1listDefinedDomains
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC_LIST_STRINGARRAY(env, obj, (virConnectPtr)VCP, virConnectListDefinedDomains, virConnectNumOfDefinedDomains)
}

JNIEXPORT jintArray JNICALL Java_org_libvirt_Connect__1listDomains
  (JNIEnv *env, jobject obj, jlong VCP){
	int maxids;
	int *ids;
	jintArray j_ids=NULL;

	if((maxids = virConnectNumOfDomains((virConnectPtr)VCP))<0)
		return NULL;
	ids= (int*)calloc(maxids, sizeof(int));
	if(virConnectListDomains((virConnectPtr)VCP, ids, maxids)>=0){
		j_ids= (jintArray)(*env)->NewIntArray(env, maxids);
		(*env)->SetIntArrayRegion(env, j_ids, 0, maxids, ids);
	}
	free(ids);

	return j_ids;
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainLookupByID
  (JNIEnv *env, jobject obj, jlong VCP, jint id){
	return (jlong)virDomainLookupByID((virConnectPtr)VCP, id);
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainLookupByName
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_name){
	GENERIC_LOOKUPBY_STRING(env, obj, (virConnectPtr)VCP, j_name, virDomainLookupByName)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainLookupByUUID
  (JNIEnv *env, jobject obj, jlong VCP, jintArray j_UUID){
	GENERIC_LOOKUPBY_UUID(env, obj, (virConnectPtr)VCP, j_UUID, virDomainLookupByUUID)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainLookupByUUIDString
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_UUID){
	GENERIC_LOOKUPBY_STRING(env, obj, (virConnectPtr)VCP, j_UUID, virDomainLookupByUUIDString)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virGetLibVirVersion
  (JNIEnv *env, jobject obj){
	return LIBVIR_VERSION_NUMBER;
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virGetHypervisorVersion
  (JNIEnv *env, jobject obj, jstring j_type){
	unsigned long libVer;
	const char *type;
	unsigned long typeVer;

	type = (*env)->GetStringUTFChars(env, j_type, NULL);

	virGetVersion(&libVer, type, &typeVer);
	(*env)->ReleaseStringUTFChars(env, j_type, type);

	return libVer;
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainCreateLinux
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_xmlDesc, jint flags){
	GENERIC_VIROBJ_STRING_INT__VIROBJ(env, obj, (virConnectPtr)VCP, j_xmlDesc, flags, virDomainCreateLinux)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainDefineXML
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_xmlDesc){
	GENERIC_VIROBJ_STRING__VIROBJ(env, obj, (virConnectPtr)VCP, j_xmlDesc, virDomainDefineXML)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1virDomainRestore
  (JNIEnv *env, jobject obj, jlong VCP, jstring j_from){
	GENERIC_VIROBJ_STRING__VIROBJ(env, obj, (virConnectPtr)VCP, j_from, virDomainRestore)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1setDom0Memory
  (JNIEnv *env, jobject obj, jlong memory){
	return virDomainSetMemory(NULL, memory);
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfDefinedStoragePools
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__INT(env, obj, (virConnectPtr)VCP, virConnectNumOfDefinedStoragePools)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfStoragePools
  (JNIEnv *env, jobject obj, jlong VCP){
	GENERIC__VIROBJ__INT(env, obj, (virConnectPtr)VCP, virConnectNumOfStoragePools)
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Connect__1listDefinedStoragePools
(JNIEnv *env, jobject obj, jlong VCP){
	GENERIC_LIST_STRINGARRAY(env, obj, (virConnectPtr)VCP, virConnectListDefinedStoragePools, virConnectNumOfDefinedStoragePools)
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Connect__1listStoragePools
(JNIEnv *env, jobject obj, jlong VCP){
	GENERIC_LIST_STRINGARRAY(env, obj, (virConnectPtr)VCP, virConnectListStoragePools, virConnectNumOfStoragePools)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virStoragePoolCreateXML
(JNIEnv *env, jobject obj, jlong VCP, jstring j_xmlDesc, jint flags){
	GENERIC_VIROBJ_STRING_INT__VIROBJ(env, obj, (virConnectPtr)VCP, j_xmlDesc, flags, virStoragePoolCreateXML)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virStoragePoolDefineXML
(JNIEnv *env, jobject obj, jlong VCP, jstring j_xmlDesc, jint flags){
	GENERIC_VIROBJ_STRING_INT__VIROBJ(env, obj, (virConnectPtr)VCP, j_xmlDesc, flags, virStoragePoolDefineXML)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virStoragePoolLookupByName
(JNIEnv *env, jobject obj, jlong VCP, jstring j_name){
	GENERIC_LOOKUPBY_STRING(env, obj, (virConnectPtr)VCP, j_name, virStoragePoolLookupByName)
}

