#include "org_libvirt_Network.h"
#include <libvirt/libvirt.h>
#include "generic.h"
#include <stdlib.h>

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getXMLDesc
  (JNIEnv *env, jobject obj, jlong VNP, jint j_flags){
	GENERIC_VIROBJ_INT__STRING(env, obj, (virNetworkPtr)VNP, j_flags, virNetworkGetXMLDesc)
};

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1create
  (JNIEnv *env, jobject obj, jlong VNP){
	GENERIC__VIROBJ__INT(env, obj, (virNetworkPtr)VNP, virNetworkCreate)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1destroy
(JNIEnv *env, jobject obj, jlong VNP){
	GENERIC__VIROBJ__INT(env, obj, (virNetworkPtr)VNP, virNetworkDestroy)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1free
(JNIEnv *env, jobject obj, jlong VNP){
	GENERIC__VIROBJ__INT(env, obj, (virNetworkPtr)VNP, virNetworkFree)
}

JNIEXPORT jboolean JNICALL Java_org_libvirt_Network__1getAutostart
  (JNIEnv *env, jobject obj, jlong VNP){
	GENERIC_GETAUTOSTART(env, obj, (virNetworkPtr)VNP, virNetworkGetAutostart)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1setAutostart
  (JNIEnv *env, jobject obj, jlong VNP, jboolean j_autostart){
	GENERIC__VIROBJ_INT__INT(env, obj, (virNetworkPtr)VNP, j_autostart, virNetworkSetAutostart)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getBridgeName
  (JNIEnv *env, jobject obj, jlong VNP){
	GENERIC__VIROBJ__STRING(env, obj, (virNetworkPtr)VNP, virNetworkGetBridgeName)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getName
  (JNIEnv *env, jobject obj, jlong VNP){
	GENERIC__VIROBJ__CONSTSTRING(env, obj, (virNetworkPtr)VNP, virNetworkGetName)
}

JNIEXPORT jintArray JNICALL Java_org_libvirt_Network__1getUUID
  (JNIEnv *env, jobject obj, jlong VNP){
	GENERIC_GETUUID(env, obj, (virNetworkPtr)VNP, virNetworkGetUUID)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Network__1getUUIDString
(JNIEnv *env, jobject obj, jlong VNP){
	GENERIC_GETUUIDSTRING(env, obj, (virNetworkPtr)VNP, virNetworkGetUUIDString)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Network__1undefine
  (JNIEnv *env, jobject obj, jlong VNP){
	GENERIC__VIROBJ__INT(env, obj, (virNetworkPtr)VNP, virNetworkUndefine)
}
