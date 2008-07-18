#include "org_libvirt_Connect.h"
#include <libvirt/libvirt.h>
#include <stdlib.h>
#include "ErrorHandler.h"
#include "ConnectAuthCallbackBridge.h"

#include <assert.h>

//TODO We are leaking UTFChars all over the place. We need to strcpy, then release every string we get from JAVA, and not use them directly!

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1virInitialize
  (JNIEnv *env, jclass cls){
	int result;
	result=virInitialize();
	//The connection-less errors go to the initializing thread as an exception.
	//Not ideal, but better than just dropping the errors.
	virSetErrorFunc(env, virErrorHandler);
	return result;
}

JNIEXPORT void JNICALL Java_org_libvirt_Connect__1close
  (JNIEnv *env, jobject obj, jlong VCP){
	virConnectClose( (virConnectPtr)VCP );
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Connect__1getHostName
  (JNIEnv *env, jobject obj, jlong VCP){
	//All this gymnastics is so that we can free() the hostname string
	jstring j_hostname=NULL;
	char *hostname;
	if((hostname = virConnectGetHostname((virConnectPtr)VCP))){
		j_hostname = (*env)->NewStringUTF(env, hostname);
		free(hostname);
	}
	return j_hostname;
};

JNIEXPORT jstring JNICALL Java_org_libvirt_Connect__1getCapabilities
  (JNIEnv *env, jobject obj, jlong VCP){
	jstring j_capabilities=NULL;
	char *capabilities;
	if((capabilities = virConnectGetCapabilities((virConnectPtr)VCP))){
		j_capabilities = (*env)->NewStringUTF(env, capabilities);
		free(capabilities);
	}
	return j_capabilities;
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1getMaxVcpus
  (JNIEnv *env, jobject obj, jlong VCP, jstring type){
	return virConnectGetMaxVcpus((virConnectPtr)VCP , (*env)->GetStringUTFChars(env, type, NULL));
};

JNIEXPORT jstring JNICALL Java_org_libvirt_Connect__1getType
  (JNIEnv *env, jobject obj, jlong VCP){
	const char *type;
	//Here we get a static string, no need to free()
	if((type=virConnectGetType((virConnectPtr)VCP))){
		return (*env)->NewStringUTF(env, type);
	} else {
		return NULL;
	}
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
	int retval = virConnectGetVersion((virConnectPtr)VCP, &hvVer);
	return (jlong)(hvVer);
};

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Connect__1listDefinedNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	int maxnames;
	char **names;
	int c;
	jobjectArray j_names=NULL;
	if((maxnames = virConnectNumOfDefinedNetworks((virConnectPtr)VCP))<0)
		return NULL;
	names= (char**)calloc(maxnames, sizeof(char*));
	if(virConnectListDefinedNetworks((virConnectPtr)VCP, names, maxnames)>=0){
		j_names= (jobjectArray)(*env)->NewObjectArray(env, maxnames,
			(*env)->FindClass(env,"java/lang/String"),
			(*env)->NewStringUTF(env,""));
		for(c=0; c<maxnames; c++){
			(*env)->SetObjectArrayElement(env, j_names, c, (*env)->NewStringUTF(env, names[c]));
		}
	}
	free(names);

	return j_names;
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Connect__1listNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	int maxnames;
	char **names;
	int c;
	jobjectArray j_names=NULL;
	if((maxnames = virConnectNumOfNetworks((virConnectPtr)VCP))<0)
		return NULL;
	names= (char**)calloc(maxnames, sizeof(char*));
	if(virConnectListNetworks((virConnectPtr)VCP, names, maxnames)>=0){
		j_names= (jobjectArray)(*env)->NewObjectArray(env, maxnames,
			(*env)->FindClass(env,"java/lang/String"),
			(*env)->NewStringUTF(env,""));
		for(c=0; c<maxnames; c++){
			(*env)->SetObjectArrayElement(env, j_names, c, (*env)->NewStringUTF(env, names[c]));
		}
	}
	free(names);

	return j_names;
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfDefinedDomains
  (JNIEnv *env, jobject obj, jlong VCP){
	return virConnectNumOfDefinedDomains((virConnectPtr)VCP);
};

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfDefinedNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	return virConnectNumOfDefinedNetworks((virConnectPtr)VCP);
};

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfDomains
  (JNIEnv *env, jobject obj, jlong VCP){
	return virConnectNumOfDomains((virConnectPtr)VCP);
};

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1numOfNetworks
  (JNIEnv *env, jobject obj, jlong VCP){
	return virConnectNumOfNetworks((virConnectPtr)VCP);
};


JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1open
  (JNIEnv *env, jobject obj, jstring uri){

	virConnectPtr vc;
	virError error;

	//Initialize the libvirt VirtConn Object
	vc=virConnectOpen((*env)->GetStringUTFChars(env, uri, NULL));
	if(vc==NULL){
		virCopyLastError(&error);
		virErrorHandler(env, &error);
		return (jlong)NULL;
	}

	//Initialized the error handler for this connection
	virConnSetErrorFunc(vc, env, virErrorHandler);

	return (jlong)vc;
};

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1openReadOnly
  (JNIEnv *env, jobject obj, jstring uri){

	virConnectPtr vc;
	virError error;

	//Initialize the libvirt VirtConn Object
	vc=virConnectOpenReadOnly((*env)->GetStringUTFChars(env, uri, NULL));
	if(vc==NULL){
		virCopyLastError(&error);
		virErrorHandler(env, &error);
		return (jlong)NULL;
	}

	//Initialized the error handler for this connection
	virConnSetErrorFunc(vc, env, virErrorHandler);

	return (jlong)vc;
};

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1openAuth
  (JNIEnv *env, jobject obj, jstring uri, jobject j_auth, jint flags){

	virConnectPtr vc;
	virError error;

	virConnectAuth *auth = malloc(sizeof(virConnectAuth));

	jobject j_credTypeElement;
	int c;

fprintf(stderr, "In 1openAuth\n");

	//Prepare by computing the class and field IDs
	jfieldID credTypeArray_id = (*env)->GetFieldID(env,
			(*env)->FindClass(env, "org/libvirt/ConnectAuth"),
			"credType",
			"[Lorg/libvirt/ConnectAuth$CredentialType;");
	jmethodID credTypeMapToInt_id = (*env)->GetMethodID(env,
			(*env)->FindClass(env, "org/libvirt/ConnectAuth$CredentialType"),
			"mapToInt",
			"()I");

fprintf(stderr, "FindClass done\n");

	//Copy the array of credtypes with the helper function
	jarray j_credTypeArray=(*env)->GetObjectField(env, j_auth, credTypeArray_id);
	auth->ncredtype = (*env)->GetArrayLength(env, j_credTypeArray);

	auth->credtype = calloc(auth->ncredtype, sizeof(int));
	for(c=0; c< auth->ncredtype; c++){
		j_credTypeElement = (*env)->GetObjectArrayElement(env, j_credTypeArray, c);
		auth->credtype[c]=(*env)->CallIntMethod(env, j_credTypeElement, credTypeMapToInt_id);
	}

fprintf(stderr, "Array copied\n");

	//The callback function is always ConnectAuthCallbackBridge
	auth->cb = &ConnectAuthCallbackBridge;
	//We pass the ConnectAuth object and the JNI env in cdbata
	CallBackStructType* cb_wrapper;
	cb_wrapper = malloc(sizeof(CallBackStructType));
	cb_wrapper->env = env;
	cb_wrapper->auth = j_auth;
	auth->cbdata=cb_wrapper;

fprintf(stderr, "calling virConnectOpenAuth\n");

	vc=virConnectOpenAuth((*env)->GetStringUTFChars(env, uri, NULL), auth, flags);
	if(vc==NULL){

fprintf(stderr, "Got NULL\n");

		virCopyLastError(&error);
		virErrorHandler(env, &error);
		return (jlong)NULL;
	}

	//Initialize the error handler for this connection
	virConnSetErrorFunc(vc, env, virErrorHandler);

fprintf(stderr, "finish 1openAuth\n");
	return (jlong)vc;
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkCreateXML
  (JNIEnv *env, jobject obj, jlong VCP, jstring xmlDesc){
	return (jlong)virNetworkCreateXML((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, xmlDesc, NULL));
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkDefineXML
(JNIEnv *env, jobject obj, jlong VCP, jstring xmlDesc){
	return (jlong)virNetworkDefineXML((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, xmlDesc, NULL));
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkLookupByName
  (JNIEnv *env, jobject obj, jlong VCP, jstring name){
	return (jlong)virNetworkLookupByName((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, name, NULL));
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkLookupByUUID
  (JNIEnv *env, jobject obj, jlong VCP, jintArray j_UUID){
	unsigned char UUID[VIR_UUID_BUFLEN];
	int c;
	int *UUID_int  = (*env)->GetIntArrayElements(env, j_UUID, NULL);
	//compact to bytes
	for(c=0; c < VIR_UUID_BUFLEN; c++)
		UUID[c]=UUID_int[c];
	return (jlong)virNetworkLookupByUUID((virConnectPtr)VCP, UUID);
}


JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virNetworkLookupByUUIDString
  (JNIEnv *env, jobject obj, jlong VCP, jstring UUID){
	return (jlong)virNetworkLookupByUUIDString((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, UUID, NULL));
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
	int maxnames;
	char **names;
	int c;
	jobjectArray j_names=NULL;
	if((maxnames = virConnectNumOfDefinedDomains((virConnectPtr)VCP))<0)
		return NULL;
	names= (char**)calloc(maxnames, sizeof(char*));
	if(virConnectListDefinedDomains((virConnectPtr)VCP, names, maxnames)>=0){
		j_names= (jobjectArray)(*env)->NewObjectArray(env, maxnames,
			(*env)->FindClass(env,"java/lang/String"),
			(*env)->NewStringUTF(env,""));
		for(c=0; c<maxnames; c++){
			(*env)->SetObjectArrayElement(env, j_names, c, (*env)->NewStringUTF(env, names[c]));
		}
	}
	free(names);

	return j_names;
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
  (JNIEnv *env, jobject obj, jlong VCP, jstring name){
		return (jlong)virDomainLookupByName((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, name, NULL));
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainLookupByUUID
  (JNIEnv *env, jobject obj, jlong VCP, jintArray j_UUID){
	unsigned char UUID[VIR_UUID_BUFLEN];
	int c;
	int *UUID_int  = (*env)->GetIntArrayElements(env, j_UUID, NULL);
	//compact to bytes
	for(c=0; c < VIR_UUID_BUFLEN; c++)
		UUID[c]=UUID_int[c];
	return (jlong)virDomainLookupByUUID((virConnectPtr)VCP, UUID);
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainLookupByUUIDString
  (JNIEnv *env, jobject obj, jlong VCP, jstring UUID){
	return (jlong)virDomainLookupByUUIDString((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, UUID, NULL));
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

	return libVer;
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainCreateLinux
  (JNIEnv *env, jobject obj, jlong VCP, jstring xmlDesc, jint flags){
	return(jlong)virDomainCreateLinux((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, xmlDesc, NULL), flags);
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Connect__1virDomainDefineXML
  (JNIEnv *env, jobject obj, jlong VCP, jstring xmlDesc){
	return(jlong)virDomainDefineXML((virConnectPtr)VCP, (*env)->GetStringUTFChars(env, xmlDesc, NULL));
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1virDomainRestore
  (JNIEnv *env, jobject obj, jlong VCP, jstring from){
	return virDomainRestore((virConnectPtr)VCP, (char*)(*env)->GetStringUTFChars(env, from, NULL));
}

JNIEXPORT jint JNICALL Java_org_libvirt_Connect__1setDom0Memory
  (JNIEnv *env, jobject obj, jlong memory){
	return virDomainSetMemory(NULL, memory);
}
