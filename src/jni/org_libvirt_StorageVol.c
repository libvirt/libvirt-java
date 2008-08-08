#include "org_libvirt_StorageVol.h"
#include "generic.h"
#include <libvirt/libvirt.h>

JNIEXPORT jlong JNICALL Java_org_libvirt_StorageVol__1storagePoolLookupByVolume
  (JNIEnv *env, jobject obj, jlong VSVP){
	GENERIC_LOOKUPBY_NONE(env, obj, (virStorageVolPtr)VSVP, virStoragePoolLookupByVolume)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StorageVol__1delete
  (JNIEnv *env, jobject obj, jlong VSVP, jint flags){
	GENERIC__VIROBJ_INT__INT(env, obj, (virStorageVolPtr)VSVP, flags, virStorageVolDelete)
}

JNIEXPORT jint JNICALL Java_org_libvirt_StorageVol__1free
  (JNIEnv *env, jobject obj, jlong VSVP){
	GENERIC__VIROBJ__INT(env, obj, (virStorageVolPtr)VSVP, virStorageVolFree)
}

JNIEXPORT jobject JNICALL Java_org_libvirt_StorageVol__1getInfo
(JNIEnv *env, jobject obj, jlong VSVP){

	virStorageVolInfo storageVolInfo;

	jobject j_info;

	//Get the data
	if(virStorageVolGetInfo((virStorageVolPtr)VSVP, &storageVolInfo)<0)
		return NULL;

	//get the field Ids of info
	jclass j_storageVolInfo_cls = (*env)->FindClass(env,"org/libvirt/StorageVolInfo");
	jmethodID j_storageVolInfo_constructor = (*env)->GetMethodID(env, j_storageVolInfo_cls, "<init>", "(IJJ)V");

	//Long live encapsulation
	j_info=(*env)->NewObject(env,
					j_storageVolInfo_cls,
					j_storageVolInfo_constructor,
					storageVolInfo.type,
					storageVolInfo.capacity,
					storageVolInfo.allocation);

	return j_info;
}

JNIEXPORT jstring JNICALL Java_org_libvirt_StorageVol__1getKey
  (JNIEnv *env, jobject obj, jlong VSVP){
	GENERIC__VIROBJ__CONSTSTRING(env, obj, (virStorageVolPtr)VSVP, virStorageVolGetKey)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_StorageVol__1getName
  (JNIEnv *env, jobject obj, jlong VSVP){
	GENERIC__VIROBJ__CONSTSTRING(env, obj, (virStorageVolPtr)VSVP, virStorageVolGetName)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_StorageVol__1getPath
  (JNIEnv *env, jobject obj, jlong VSVP){
	GENERIC__VIROBJ__STRING(env, obj, (virStorageVolPtr)VSVP, virStorageVolGetPath)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_StorageVol__1getXMLDesc
  (JNIEnv *env, jobject obj, jlong VSVP, jint flags){
	GENERIC_VIROBJ_INT__STRING(env, obj, (virStorageVolPtr)VSVP, flags, virStorageVolGetXMLDesc)
}

