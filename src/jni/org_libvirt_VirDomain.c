#include "org_libvirt_VirDomain.h"
#include <libvirt/libvirt.h>
#include <stdlib.h>
#include <string.h>

JNIEXPORT jstring JNICALL Java_org_libvirt_VirDomain__1getXMLDesc
  (JNIEnv *env, jobject obj, jlong VDP, jint flags){
	jstring j_xmlDesc;
	char* xmlDesc = NULL;
	if((xmlDesc = virDomainGetXMLDesc((virDomainPtr)VDP, flags))){
		j_xmlDesc = (*env)->NewStringUTF(env, xmlDesc);
		free(xmlDesc);
	}
	return j_xmlDesc;
}

JNIEXPORT jboolean JNICALL Java_org_libvirt_VirDomain__1getAutostart
  (JNIEnv *env, jobject obj, jlong VDP){
	int autostart=0;
	virDomainGetAutostart((virDomainPtr)VDP, &autostart);
	return (jboolean)autostart;
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1setAutostart
  (JNIEnv *env, jobject obj, jlong VDP, jboolean autostart){
	return virDomainSetAutostart((virDomainPtr)VDP, (int)autostart);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1getID
  (JNIEnv *env, jobject obj, jlong VDP){
	return virDomainGetID((virDomainPtr)VDP);
}

JNIEXPORT jlong JNICALL Java_org_libvirt_VirDomain__1getMaxMemory
(JNIEnv *env, jobject obj, jlong VDP){
	return virDomainGetMaxMemory((virDomainPtr)VDP);
}

JNIEXPORT jlong JNICALL Java_org_libvirt_VirDomain__1setMaxMemory
  (JNIEnv *env, jobject obj, jlong VDP, jlong memory){
	return virDomainSetMaxMemory((virDomainPtr)VDP, memory);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1getMaxVcpus
  (JNIEnv *env, jobject obj, jlong VDP){
	return virDomainGetMaxVcpus((virDomainPtr)VDP);
}

JNIEXPORT jstring JNICALL Java_org_libvirt_VirDomain__1getName
  (JNIEnv *env, jobject obj, jlong VDP){
	return (*env)->NewStringUTF(env, virDomainGetName((virDomainPtr)VDP));
}

JNIEXPORT jstring JNICALL Java_org_libvirt_VirDomain__1getOSType
  (JNIEnv *env, jobject obj, jlong VDP){
	jstring j_OSType;
	char *OSType;
	
	if((OSType = virDomainGetOSType((virDomainPtr)VDP))){
		j_OSType = (*env)->NewStringUTF(env, OSType);
		free(OSType);
	}
	return j_OSType;
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_VirDomain__1getSchedulerType
(JNIEnv *env, jobject obj, jlong VDP){
	jstring j_schedulerType;
	char *schedulerType=NULL;
	int nparams;
	
	//We don't return nparams
	if((schedulerType = virDomainGetSchedulerType((virDomainPtr)VDP, &nparams))){
		j_schedulerType = (*env)->NewStringUTF(env, schedulerType);
		free(schedulerType);
	}
	return j_schedulerType;
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_VirDomain__1getSchedulerParameters
  (JNIEnv *env, jobject obj, jlong VDP){
	//It's gonna be slightly^h^h^h^h^h^h^h^hvery painful
	int nparams;
	int c;
	char *schedulerType;
	virSchedParameterPtr params;
	jobjectArray j_params;
	jobject j_param;
	jobject j_field;
	jclass cls;
	
	//Get nparams
	if(!(schedulerType = virDomainGetSchedulerType((virDomainPtr)VDP, &nparams)))
		return NULL;
	
	free(schedulerType);
	
	//allocate space for params
	params=(virSchedParameterPtr)calloc((size_t)nparams, (size_t)sizeof(virSchedParameter));
	
	//Fill it
	if(virDomainGetSchedulerParameters((virDomainPtr)VDP, params, &nparams)<0)
		return NULL;
	
	//We need a dummy element to initialize the array
	j_param = (*env)->NewObject(env,
			(*env)->FindClass(env,"org/libvirt/VirSchedIntParameter"),
			(*env)->GetMethodID(env, (*env)->FindClass(env,"org/libvirt/VirSchedIntParameter"), "<init>", "()V"));
	
	//Create the array
	j_params= (jobjectArray)(*env)->NewObjectArray(env, nparams,
			(*env)->FindClass(env,"org/libvirt/VirSchedParameter"),
			j_param);
	
	//Fill it
	for(c=0; c<nparams; c++){
		j_field = (*env)->NewStringUTF(env, params[c].field);
		switch(params[c].type){
		case    VIR_DOMAIN_SCHED_FIELD_INT:
			cls = (*env)->FindClass(env,"org/libvirt/VirSchedIntParameter");
			//Do I really need to allocate a new one every time?
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "I"), params[c].value.i);
			break;
		case    VIR_DOMAIN_SCHED_FIELD_UINT:
			cls = (*env)->FindClass(env,"org/libvirt/VirSchedUintParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "I"), params[c].value.ui);
			break;
		case    VIR_DOMAIN_SCHED_FIELD_LLONG:
			cls = (*env)->FindClass(env,"org/libvirt/VirSchedLongParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "J"), params[c].value.l);
			break;
		case	VIR_DOMAIN_SCHED_FIELD_ULLONG:
			cls = (*env)->FindClass(env,"org/libvirt/VirSchedUlongParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "J"), params[c].value.ul);
			break;
		case	VIR_DOMAIN_SCHED_FIELD_DOUBLE:
			cls = (*env)->FindClass(env,"org/libvirt/VirSchedDoubleParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "D"), params[c].value.d);
			break;
		case    VIR_DOMAIN_SCHED_FIELD_BOOLEAN:
			cls = (*env)->FindClass(env,"org/libvirt/VirSchedBooleanParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "Z"), params[c].value.b);
		}
		//Copy our shiny new object to the array
		(*env)->SetObjectArrayElement(env, j_params, c, j_param);
	}
	//free params;
	free(params);
	
	//Crash and burn
	return j_params;
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1setSchedulerParameters
  (JNIEnv *env, jobject obj, jlong VDP, jobjectArray j_params){
	int nparams=(*env)->GetArrayLength(env, j_params);
	virSchedParameterPtr params; 
	jobject j_param;
	
	jfieldID field_id;
	jfieldID value_id;
	int c;
	int returnvalue;
	
	params = (virSchedParameterPtr)calloc(sizeof(virSchedParameter),c);
	
	for(c=0; c<nparams; c++){
		j_param= (*env)->GetObjectArrayElement(env, j_params, c);
		
		if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/VirSchedIntParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "I");
			strcpy(params[c].field,(char*)(*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL));
			params[c].value.i= (*env)->GetIntField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_INT;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/VirSchedUintParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "I");
			strcpy(params[c].field,(char*)(*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL));
			params[c].value.ui= (*env)->GetIntField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_UINT;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/VirSchedLongParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "J");
			strcpy(params[c].field,(char*)(*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL));
			params[c].value.l= (*env)->GetLongField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_LLONG;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/VirSchedUlongParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "J");
			strcpy(params[c].field,(char*)(*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL));
			params[c].value.ul= (*env)->GetLongField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_ULLONG;	
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/VirSchedDoubleParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "D");
			strcpy(params[c].field,(char*)(*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL));
			params[c].value.d= (*env)->GetDoubleField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_ULLONG;	
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/VirSchedBooleanParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "Z");
			strcpy(params[c].field,(char*)(*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL));
			params[c].value.b= (*env)->GetBooleanField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_BOOLEAN;
		}
	}
	
	returnvalue= virDomainSetSchedulerParameters((virDomainPtr)VDP, params, nparams);
	free(params);
	
	return returnvalue;
}

JNIEXPORT jintArray JNICALL Java_org_libvirt_VirDomain__1getUUID
  (JNIEnv *env, jobject obj, jlong VDP){
	unsigned char uuid[VIR_UUID_BUFLEN];
	jintArray j_uuid;
	int c;
	int uuidbyte;
	
	if(virDomainGetUUID((virDomainPtr)VDP, uuid)<0)
		return NULL;
	//unpack UUID
	j_uuid=(*env)->NewIntArray(env, VIR_UUID_BUFLEN);
	for(c=0; c<VIR_UUID_BUFLEN; c++){
			uuidbyte=uuid[c];
			(*env)->SetIntArrayRegion(env, j_uuid, c, 1, &uuidbyte);
	}
	return j_uuid;
}

JNIEXPORT jstring JNICALL Java_org_libvirt_VirDomain__1getUUIDString
  (JNIEnv *env, jobject obj, jlong VDP){
	char uuidString[VIR_UUID_STRING_BUFLEN];
	virDomainGetUUIDString((virDomainPtr)VDP, uuidString);
	return (*env)->NewStringUTF(env, uuidString);
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_VirDomain__1getVcpusInfo
  (JNIEnv *env, jobject obj, jlong VDP){
	//Please hurt me even more!
	int maxinfo;
	virVcpuInfoPtr info;
	int c;
	
	jobject j_info;
	jobjectArray j_infoArray=NULL;
	
	jfieldID number_id;
	jfieldID state_id;
	jfieldID cputime_id;
	jfieldID cpu_id;
	
	jmethodID state_values_id;
	jclass state_class;
	jclass vcpuinfo_class;
	jobjectArray state_values;

	//Check the number of vcpus
	if((maxinfo=virDomainGetMaxVcpus((virDomainPtr)VDP))<0)
		return NULL;
	
	info=(virVcpuInfoPtr)calloc(maxinfo, sizeof(virVcpuInfo));
	
	//Get the data
	if(virDomainGetVcpus((virDomainPtr)VDP, info, maxinfo, NULL, 0)>=0){
		//get the field Ids of info
		vcpuinfo_class = (*env)->FindClass(env,"org/libvirt/VirVcpuInfo");
		number_id = (*env)->GetFieldID(env, vcpuinfo_class, "number", "I");
		state_id = (*env)->GetFieldID(env, vcpuinfo_class, "state", "Lorg/libvirt/VirVcpuInfo$VirVcpuState;");
		cputime_id =  (*env)->GetFieldID(env, vcpuinfo_class, "cputime", "J");
		cpu_id =  (*env)->GetFieldID(env, vcpuinfo_class, "cpu", "I");
		
		//Get objects for the states so that we can copy them into the info structure
		state_class=(*env)->FindClass(env,"org.libvirt.VirVcpuInfo$VirVcpuState");
		state_values_id=(*env)->GetStaticMethodID(env, state_class, "values", "()[Lorg/libvirt/VirVcpuInfo$VirVcpuState");
		state_values=(*env)->CallStaticObjectMethod(env, state_class, state_values_id);
		
		//We need a dummy element to initialize the array
		j_info = (*env)->NewObject(env,
				(*env)->FindClass(env,"org/libvirt/VirVcpuInfo"),
				(*env)->GetMethodID(env, (*env)->FindClass(env,"org/libvirt/VirVcpuInfo"), "<init>", "()V"));
	
		//Create the info array
		j_infoArray= (jobjectArray)(*env)->NewObjectArray(env, maxinfo,
				(*env)->FindClass(env,"org/libvirt/VirVcpuInfo"),
				j_info);
		
		//Fill it
		for(c=0; c<maxinfo; c++){
			//Does SteObjectArrayElement copy or reference this? Do I really need to allocate the objects? I think not 
			j_info = (*env)->AllocObject(env, vcpuinfo_class);
			//Fill the fields
			(*env)->SetIntField(env, j_info, number_id, info[c].number);
			(*env)->SetObjectField(env, j_info, state_id,
					(*env)->GetObjectArrayElement(env, state_values, info[c].state));
			(*env)->SetLongField(env, j_info, cputime_id, info[c].cpuTime);
			(*env)->SetIntField(env, j_info, cpu_id, info[c].cpu);
			//Add to the array
			(*env)->SetObjectArrayElement(env, j_infoArray, c, j_info);
		}
	}
	free(info);	
	return j_infoArray;
}

JNIEXPORT jintArray JNICALL Java_org_libvirt_VirDomain__1getVcpusCpuMaps
  (JNIEnv *env, jobject obj, jlong VDP){
	int maxinfo;
	int maplen;
	unsigned char *cpumaps;
	int *i_cpumaps;
	jintArray j_cpumaps;
	int c;
	virNodeInfoPtr nodeinfo;
	virVcpuInfoPtr info;
	
	//Check number of vcpus;
	if((maxinfo = virDomainGetMaxVcpus((virDomainPtr)VDP))<0)
		return NULL;
	
	//Get maplen
	if(virNodeGetInfo( virDomainGetConnect( (virDomainPtr)VDP), nodeinfo )<0)
		return NULL;
	maplen=VIR_CPU_MAPLEN( VIR_NODEINFO_MAXCPUS( *nodeinfo ) );
	
	info=(virVcpuInfoPtr)calloc(maxinfo, sizeof(virVcpuInfo));
	cpumaps=malloc(sizeof(int)*maxinfo*maplen);
	
	//Get the data
	if(virDomainGetVcpus((virDomainPtr)VDP, info, maxinfo, cpumaps, maplen)>0){
		//unpack cpumaps
		for(c=0; c<maxinfo*maplen; c++)
			i_cpumaps[c]=cpumaps[c];		
		
		j_cpumaps=(*env)->NewIntArray(env, maxinfo*maplen);
		(*env)->SetIntArrayRegion(env, j_cpumaps, 0, maxinfo*maplen, i_cpumaps);
	}
	free(info);
	free(cpumaps);

	return(j_cpumaps);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1pinVcpu
  (JNIEnv *env, jobject obj, jlong VDP, jint vcpu, jintArray j_cpumap){
	int maplen;
	unsigned char *cpumap;
	jint *i_cpumap;
	int c;
	int retval;

	//Get maplen
	maplen=(*env)->GetArrayLength(env, j_cpumap);
	
	i_cpumap=calloc(sizeof(int), maplen);
	cpumap=calloc(sizeof(unsigned char), maplen);
	
	i_cpumap=(*env)->GetIntArrayElements(env, j_cpumap, NULL);
	
	//pack cpumap
	for(c=0; c<maplen; c++)
		cpumap[c]=i_cpumap[c];
	
	//Call libvirt
	retval = virDomainPinVcpu((virDomainPtr)VDP, vcpu, cpumap, maplen);
	
	free(cpumap);
	free(i_cpumap);	
	return retval;
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1setVcpus
  (JNIEnv *env, jobject obj, jlong VDP, jint nvcpus){
	return virDomainSetVcpus((virDomainPtr)VDP, nvcpus);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1attachDevice
  (JNIEnv *env, jobject obj, jlong VDP, jstring xmlDesc){
	return (jlong)virDomainAttachDevice((virDomainPtr)VDP, (char*)(*env)->GetStringUTFChars(env, xmlDesc, NULL));
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1detachDevice
  (JNIEnv *env, jobject obj, jlong VDP, jstring xmlDesc){
	return (jlong)virDomainDetachDevice((virDomainPtr)VDP, (char*)(*env)->GetStringUTFChars(env, xmlDesc, NULL));
}

//JNIEXPORT jobject JNICALL Java_org_libvirt_VirDomain__1blockStats
//  (JNIEnv *env, jobject obj, jlong VDP, jstring j_path){
//	struct  _virDomainBlockStats stats;
//	long value;
//	jobject j_stats;
//	jclass stats_cls=(*env)->FindClass(env, "org/libvirt/VirDomainInterfaceStats");
//	jclass bigint_cls=(*env)->FindClass(env, "java/math/BigInteger");
//	jmethodID bigint_constructor=(*env)->GetMethodID(env, bigint_cls, "java.math.BigInteger", "([B)V");
//	jbyteArray bytes;
//	jobject bigint;
//		
//	if(virDomainBlockStats((virDomainPtr)VDP, (*env)->GetStringUTFChars(env, j_path, NULL), &stats, sizeof(struct  _virDomainBlockStats))<0)
//		return NULL;
//	
//	//Endianness fun. Should work on Linux. 
//	#if __BYTE_ORDER == __LITTLE_ENDIAN
//	stats.rd_req= bswap_64(stats.rd_req);
//	stats.rd_bytes= bswap_64(stats.rd_bytes);
//	stats.wr_req= bswap_64(stats.wr_req);
//	stats.wr_bytes= bswap_64(stats.wr_bytes);
//	stats.errs= bswap_64(stats.errs);
//	#endif
//	
//	j_stats = (*env)->AllocObject(env, stats_cls);
//	bytes=(*env)->NewByteArray(env,8);
//	
//	
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.rd_req));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rd_req", "Ljava/math/BigInteger;"), bigint);
//	
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.rd_bytes));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rd_bytes", "Ljava/math/BigInteger;"), bigint);
//
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.wr_req));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "wr_req", "Ljava/math/BigInteger;"), bigint);
//
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.wr_bytes));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "wr_bytes", "Ljava/math/BigInteger;"), bigint);
//
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.errs));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "errs", "Ljava/math/BigInteger;"), bigint);
//	
//	return j_stats;	
//}

JNIEXPORT jobject JNICALL Java_org_libvirt_VirDomain__1blockStats
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_path){
	struct  _virDomainBlockStats stats;
	jobject j_stats;
	jclass stats_cls=(*env)->FindClass(env, "org/libvirt/VirDomainInterfaceStats");
		
	if(virDomainBlockStats((virDomainPtr)VDP, (*env)->GetStringUTFChars(env, j_path, NULL), &stats, sizeof(struct  _virDomainBlockStats))<0)
		return NULL;
	
	j_stats = (*env)->AllocObject(env, stats_cls);
	
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rd_req", "J"), stats.rd_req);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rd_bytes", "J"), stats.rd_bytes);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "wr_req", "J"), stats.wr_req);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "wr_bytes", "J"), stats.wr_bytes);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "errs", "J"), stats.errs);
	
	return j_stats;	
}


//JNIEXPORT jobject JNICALL Java_org_libvirt_VirDomain__1interfaceStats
//  (JNIEnv *env, jobject obj, jlong VDP, jstring j_path){
//	struct  _virDomainInterfaceStats stats;
//	long value;
//	jobject j_stats;
//	jclass stats_cls=(*env)->FindClass(env, "org/libvirt/VirDomainInterfaceStats");
//	jclass bigint_cls=(*env)->FindClass(env, "java/math/BigInteger");
//	jmethodID bigint_constructor=(*env)->GetMethodID(env, bigint_cls, "java.math.BigInteger", "([B)V");
//	jbyteArray bytes;
//	jobject bigint;
//		
//	if(virDomainInterfaceStats((virDomainPtr)VDP, (*env)->GetStringUTFChars(env, j_path, NULL), &stats, sizeof(struct  _virDomainInterfaceStats))<0)
//		return NULL;
//	
//	//Endianness fun. Should work on Linux. 
//	#if __BYTE_ORDER == __LITTLE_ENDIAN
//	stats.rx_bytes= bswap_64(stats.rx_bytes);
//	stats.rx_packets= bswap_64(stats.rx_packets);
//	stats.rx_errs= bswap_64(stats.rx_errs);
//	stats.rx_drop= bswap_64(stats.rx_drop);
//	stats.tx_bytes= bswap_64(stats.tx_bytes);
//	stats.tx_packets= bswap_64(stats.tx_packets);
//	stats.tx_errs= bswap_64(stats.tx_errs);
//	stats.tx_drop= bswap_64(stats.tx_drop);
//	#endif
//	
//	j_stats = (*env)->AllocObject(env, stats_cls);
//	bytes=(*env)->NewByteArray(env,8);
//	
//	
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.rx_bytes));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_bytes", "Ljava/math/BigInteger;"), bigint);
//	
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.rx_packets));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_packets", "Ljava/math/BigInteger;"), bigint);
//
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.rx_errs));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_errs", "Ljava/math/BigInteger;"), bigint);
//
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.rx_drop));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_drop", "Ljava/math/BigInteger;"), bigint);
//
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.tx_bytes));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_bytes", "Ljava/math/BigInteger;"), bigint);
//
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.tx_packets));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_packets", "Ljava/math/BigInteger;"), bigint);
//	
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.tx_errs));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_errs", "Ljava/math/BigInteger;"), bigint);
//	
//	(*env)->SetByteArrayRegion(env, bytes, 0, 8, (unsigned char*)&(stats.tx_drop));
//	bigint=(*env)->NewObject(env, bigint_cls, bigint_constructor, bytes);
//	(*env)->SetObjectField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_drop", "Ljava/math/BigInteger;"), bigint);
//	
//	return j_stats;
//}

JNIEXPORT jobject JNICALL Java_org_libvirt_VirDomain__1interfaceStats
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_path){
	struct  _virDomainInterfaceStats stats;
	jobject j_stats;
	jclass stats_cls=(*env)->FindClass(env, "org/libvirt/VirDomainInterfaceStats");
		
	if(virDomainInterfaceStats((virDomainPtr)VDP, (*env)->GetStringUTFChars(env, j_path, NULL), &stats, sizeof(struct  _virDomainInterfaceStats))<0)
		return NULL;
	
	j_stats = (*env)->AllocObject(env, stats_cls);
	
	

	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_bytes", "J"), stats.rx_bytes);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_packets", "J"), stats.rx_packets);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_errs", "J"), stats.rx_errs);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rx_drop", "J"), stats.rx_drop);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_bytes", "J"), stats.tx_bytes);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_packets", "J"), stats.tx_packets);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_errs", "J"), stats.tx_errs);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "tx_drop", "J"), stats.tx_drop);
	
	return j_stats;
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1coreDump
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_to, jint flags){
	char *to = (char*)(*env)->GetStringUTFChars(env, j_to, NULL);
	return virDomainCoreDump((virDomainPtr)VDP, to, flags);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1create
  (JNIEnv *env, jobject obj, jlong VDP){
	return virDomainCreate((virDomainPtr)VDP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1destroy
  (JNIEnv *env, jobject obj, jlong VDP){
	return virDomainDestroy((virDomainPtr)VDP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1free
  (JNIEnv *env, jobject obj, jlong VDP){
	return virDomainFree((virDomainPtr)VDP);
}

JNIEXPORT jobject JNICALL Java_org_libvirt_VirDomain__1getInfo
  (JNIEnv *env, jobject obj, jlong VDP){
	//Please hurt me even more!
	virDomainInfo domainInfo;
	
	jobject j_info;

	jfieldID state_id;
	jfieldID maxMem_id;
	jfieldID memory_id;
	jfieldID nrVirtCpu_id;
	jfieldID cpuTime_id;
	
	jmethodID state_values_id;
	jclass state_class;
	jclass domaininfo_class;
	jobjectArray state_values;

	//Get the data
	if(virDomainGetInfo((virDomainPtr)VDP, &domainInfo)<0)
		return NULL;

	//get the field Ids of info
	domaininfo_class = (*env)->FindClass(env,"org/libvirt/VirDomainInfo");
	
	state_id = (*env)->GetFieldID(env, domaininfo_class, "state", "Lorg/libvirt/VirDomainInfo$VirDomainState;");
	maxMem_id = (*env)->GetFieldID(env, domaininfo_class, "maxMem", "J");
	memory_id =  (*env)->GetFieldID(env, domaininfo_class, "memory", "J");
	nrVirtCpu_id =  (*env)->GetFieldID(env, domaininfo_class, "nrVirtCpu", "I");
	cpuTime_id = (*env)->GetFieldID(env, domaininfo_class, "cpuTime", "J");
	
	//Get objects for the states so that we can copy them into the info structure
	state_class=(*env)->FindClass(env,"org/libvirt/VirDomainInfo$VirDomainState");
	state_values_id=(*env)->GetStaticMethodID(env, state_class, "values", "()[Lorg/libvirt/VirDomainInfo$VirDomainState;");
	state_values=(*env)->CallStaticObjectMethod(env, state_class, state_values_id);
	
	//Create the return object
	j_info = (*env)->AllocObject(env, domaininfo_class);
	
	//Fill the fields
	(*env)->SetObjectField(env, j_info, state_id,
			(*env)->GetObjectArrayElement(env, state_values, domainInfo.state));
	(*env)->SetLongField(env, j_info, maxMem_id, domainInfo.maxMem);
	(*env)->SetLongField(env, j_info, memory_id, domainInfo.memory);
	(*env)->SetIntField(env, j_info, nrVirtCpu_id, domainInfo.nrVirtCpu);
	(*env)->SetLongField(env, j_info, cpuTime_id, domainInfo.cpuTime);
	
	return j_info;
}

JNIEXPORT jlong JNICALL Java_org_libvirt_VirDomain__1migrate
  (JNIEnv *env, jobject obj, jlong VDP, jobject dconn, jlong flags, jstring j_dname, jstring j_uri, jlong bandwidth){
	
	virConnectPtr destVCP;
	
	char *dname=NULL;
	char *uri=NULL;
	
	//if String="", we pass NULL to the library
	if((*env)->GetStringLength(env, j_dname)>0)
		dname=(char*)(*env)->GetStringUTFChars(env, j_dname, NULL);

	//if String="", we pass NULL to the library
	if((*env)->GetStringLength(env, j_uri)>0)
		uri=(char*)(*env)->GetStringUTFChars(env, j_uri, NULL);

	//Extract the destination Conn Ptr
	destVCP=(virConnectPtr)(*env)->GetLongField(env, dconn, 
			(*env)->GetFieldID(env, (*env)->GetObjectClass(env, dconn), "VCP", "J"));
	
	return (long)virDomainMigrate((virDomainPtr)VDP, destVCP, flags, dname, uri, bandwidth);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1reboot
  (JNIEnv *env, jobject obj, jlong VDP, jint flags){
	return virDomainReboot((virDomainPtr)VDP, flags);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1suspend
(JNIEnv *env, jobject obj, jlong VDP){
	return virDomainSuspend((virDomainPtr)VDP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1resume
  (JNIEnv *env, jobject obj, jlong VDP){
	return virDomainResume((virDomainPtr)VDP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1save
  (JNIEnv *env, jobject obj, jlong VDP, jstring to){
	return virDomainSave((virDomainPtr)VDP, (char*)(*env)->GetStringUTFChars(env, to, NULL));
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1shutdown
  (JNIEnv *env, jobject obj, jlong VDP){
	return virDomainShutdown((virDomainPtr)VDP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1undefine
(JNIEnv *env, jobject obj, jlong VDP){
	return virDomainUndefine((virDomainPtr)VDP);
}

JNIEXPORT jint JNICALL Java_org_libvirt_VirDomain__1setMemory
  (JNIEnv *env, jobject obj, jlong VDP, jlong memory){
	return virDomainSetMemory((virDomainPtr)VDP, memory);
}

