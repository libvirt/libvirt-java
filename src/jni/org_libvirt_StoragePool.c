#include <libvirt/libvirt.h>
#include "org_libvirt_StoragePool.h"
#include "generic.h"


JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1build
  (JNIEnv *env, jobject obj, jlong VSPP, jint flags){
	GENERIC__VIROBJ_INT__INT(env, obj, (virStoragePoolPtr)VSPP, flags, virStoragePoolBuild)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1create
  (JNIEnv *env, jobject obj, jlong VSPP, jint flags){
	GENERIC__VIROBJ_INT__INT(env, obj, (virStoragePoolPtr)VSPP, flags, virStoragePoolCreate)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1delete
  (JNIEnv *env, jobject obj, jlong VSPP, jint flags){
	GENERIC__VIROBJ_INT__INT(env, obj, (virStoragePoolPtr)VSPP, flags, virStoragePoolDelete)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1destroy
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC__VIROBJ__INT(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolDestroy)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1free
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC__VIROBJ__INT(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolFree)
}

JNIEXPORT jboolean JNICALL Java_org_libvirt_StoragePool__1getAutostart
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC_GETAUTOSTART(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolGetAutostart)
}

JNIEXPORT jobject JNICALL Java_org_libvirt_StoragePool__1getInfo
  (JNIEnv *env, jobject obj, jlong VSPP){

	virStoragePoolInfo storagePoolInfo;

	jobject j_info;

	//Get the data
	if(virStoragePoolGetInfo((virStoragePoolPtr)VSPP, &storagePoolInfo)<0)
		return NULL;

	//get the field Ids of info
	jclass j_storagePoolInfo_cls = (*env)->FindClass(env,"org/libvirt/StoragePoolInfo");
	jmethodID j_storagePoolInfo_constructor = (*env)->GetMethodID(env, j_storagePoolInfo_cls, "<init>", "(IJJJ)V");

	//Long live encapsulation
	j_info=(*env)->NewObject(env,
					j_storagePoolInfo_cls,
					j_storagePoolInfo_constructor,
					storagePoolInfo.state,
					storagePoolInfo.capacity,
					storagePoolInfo.allocation,
					storagePoolInfo.available);

	return j_info;
}

JNIEXPORT jstring JNICALL Java_org_libvirt_StoragePool__1getName
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC__VIROBJ__CONSTSTRING(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolGetName)
}

JNIEXPORT jintArray JNICALL Java_org_libvirt_StoragePool__1getUUID
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC_GETUUID(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolGetUUID)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_StoragePool__1getUUIDString
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC_GETUUIDSTRING(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolGetUUIDString)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_StoragePool__1getXMLDesc
  (JNIEnv *env, jobject obj, jlong VSPP, jint flags){
	GENERIC_VIROBJ_INT__STRING(env, obj, (virStoragePoolPtr)VSPP, flags, virStoragePoolGetXMLDesc)
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_StoragePool__1listVolumes
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC_LIST_STRINGARRAY(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolListVolumes, virStoragePoolNumOfVolumes)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1numOfVolumes
  (JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC__VIROBJ__INT(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolNumOfVolumes)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1refresh
  (JNIEnv *env, jobject obj, jlong VSPP, jint flags){
	GENERIC__VIROBJ_INT__INT(env, obj, (virStoragePoolPtr)VSPP, flags, virStoragePoolRefresh)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1setAutostart
  (JNIEnv *env, jobject obj, jlong VSPP, jint flags){
	GENERIC__VIROBJ_INT__INT(env, obj, (virStoragePoolPtr)VSPP, flags, virStoragePoolSetAutostart)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StoragePool__1undefine
(JNIEnv *env, jobject obj, jlong VSPP){
	GENERIC__VIROBJ__INT(env, obj, (virStoragePoolPtr)VSPP, virStoragePoolUndefine)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_StoragePool__1storageVolLookupByName
  (JNIEnv *env, jobject obj, jlong VSPP, jstring name){
	GENERIC_LOOKUPBY_STRING(env, obj, (virStoragePoolPtr)VSPP, name, virStorageVolLookupByName)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_StoragePool__1storageVolCreateXML
  (JNIEnv *env, jobject obj, jlong VSPP, jstring j_xmlDesc, jint flags){
	GENERIC_VIROBJ_STRING_INT__VIROBJ(env, obj, (virStoragePoolPtr)VSPP, j_xmlDesc, flags, virStorageVolCreateXML)
}

