#include "org_libvirt_Network.h"
#include <libvirt/libvirt.h>
#include <stdlib.h>

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getXMLDesc
  (JNIEnv *env, jobject obj, jlong VNP, jint flags){
	jstring j_xmlDesc;
	char* xmlDesc;
	if((xmlDesc = virNetworkGetXMLDesc((virNetworkPtr)VNP, flags))){
		j_xmlDesc = (*env)->NewStringUTF(env, xmlDesc);
		free(xmlDesc);
	}
	return j_xmlDesc;
};

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1create
  (JNIEnv *env, jobject obj, jlong VNP){
	return virNetworkCreate((virNetworkPtr)VNP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1destroy
(JNIEnv *env, jobject obj, jlong VNP){
	return virNetworkDestroy((virNetworkPtr)VNP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1free
(JNIEnv *env, jobject obj, jlong VNP){
	return virNetworkFree((virNetworkPtr)VNP);
}

JNIEXPORT jboolean JNICALL Java_org_libvirt_Network__1getAutostart
  (JNIEnv *env, jobject obj, jlong VNP){
	int autostart;
	virNetworkGetAutostart((virNetworkPtr)VNP, &autostart);
	return (jboolean)autostart;
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1setAutostart
  (JNIEnv *env, jobject obj, jlong VNP, jboolean autostart){
	return virNetworkSetAutostart((virNetworkPtr)VNP, autostart);
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getBridgeName
  (JNIEnv *env, jobject obj, jlong VNP){
	jstring j_bridgeName;
	char *bridgeName=NULL;

	if((bridgeName = virNetworkGetBridgeName((virNetworkPtr)VNP))){
		j_bridgeName = (*env)->NewStringUTF(env, bridgeName);
		free(bridgeName);
	}
	return j_bridgeName;
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getName
  (JNIEnv *env, jobject obj, jlong VNP){
	return (*env)->NewStringUTF(env, virNetworkGetName((virNetworkPtr)VNP));
}

JNIEXPORT jintArray JNICALL Java_org_libvirt_Network__1getUUID
  (JNIEnv *env, jobject obj, jlong VNP){
	unsigned char uuid[VIR_UUID_BUFLEN];
	jintArray j_uuid;
	int c;
	int uuidbyte[VIR_UUID_BUFLEN];

	if(virNetworkGetUUID((virNetworkPtr)VNP, uuid)<0)
		return NULL;
	//unpack UUID
	j_uuid=(*env)->NewIntArray(env, VIR_UUID_BUFLEN);
	for(c=0; c<VIR_UUID_BUFLEN; c++){
			uuidbyte[c]=uuid[c];
	}
	(*env)->SetIntArrayRegion(env, j_uuid, 0, VIR_UUID_BUFLEN, uuidbyte);

	return j_uuid;
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getUUIDString
(JNIEnv *env, jobject obj, jlong VNP){
	char uuidString[VIR_UUID_STRING_BUFLEN];
	virNetworkGetUUIDString((virNetworkPtr)VNP, uuidString);
	return (*env)->NewStringUTF(env, uuidString);
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1undefine
  (JNIEnv *env, jobject obj, jlong VNP){
	return virNetworkUndefine((virNetworkPtr)VNP);
}
