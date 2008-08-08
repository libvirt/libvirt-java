#include "org_libvirt_Domain.h"
#include <libvirt/libvirt.h>
#include "generic.h"
#include <string.h>

//TODO We still leak UTFstrings in the more complex functions

JNIEXPORT jstring JNICALL Java_org_libvirt_Domain__1getXMLDesc
  (JNIEnv *env, jobject obj, jlong VDP, jint flags){
	GENERIC_VIROBJ_INT__STRING(env, obj, (virDomainPtr)VDP, flags, virDomainGetXMLDesc)
}

JNIEXPORT jboolean JNICALL Java_org_libvirt_Domain__1getAutostart
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC_GETAUTOSTART(env, obj, (virDomainPtr)VDP, virDomainGetAutostart)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1setAutostart
  (JNIEnv *env, jobject obj, jlong VDP, jboolean autostart){
	GENERIC__VIROBJ_INT__INT(env, obj, (virDomainPtr)VDP, autostart, virDomainSetAutostart)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1getID
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainGetID)
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Domain__1getMaxMemory
(JNIEnv *env, jobject obj, jlong VDP){
	return virDomainGetMaxMemory((virDomainPtr)VDP);
}

JNIEXPORT jlong JNICALL Java_org_libvirt_Domain__1setMaxMemory
  (JNIEnv *env, jobject obj, jlong VDP, jlong memory){
	return virDomainSetMaxMemory((virDomainPtr)VDP, memory);
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1getMaxVcpus
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainGetMaxVcpus)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Domain__1getName
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__CONSTSTRING(env, obj, (virDomainPtr)VDP, virDomainGetName)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Domain__1getOSType
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__STRING(env, obj, (virDomainPtr)VDP, virDomainGetOSType)
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Domain__1getSchedulerType
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

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Domain__1getSchedulerParameters
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
			(*env)->FindClass(env,"org/libvirt/SchedIntParameter"),
			(*env)->GetMethodID(env, (*env)->FindClass(env,"org/libvirt/SchedIntParameter"), "<init>", "()V"));

	//Create the array
	j_params= (jobjectArray)(*env)->NewObjectArray(env, nparams,
			(*env)->FindClass(env,"org/libvirt/SchedParameter"),
			j_param);

	//Fill it
	for(c=0; c<nparams; c++){
		j_field = (*env)->NewStringUTF(env, params[c].field);

		switch(params[c].type){
		case    VIR_DOMAIN_SCHED_FIELD_INT:
			cls = (*env)->FindClass(env,"org/libvirt/SchedIntParameter");
			//Do I really need to allocate a new one every time?
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "I"), params[c].value.i);
			break;
		case    VIR_DOMAIN_SCHED_FIELD_UINT:
			cls = (*env)->FindClass(env,"org/libvirt/SchedUintParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "I"), params[c].value.ui);
			break;
		case    VIR_DOMAIN_SCHED_FIELD_LLONG:
			cls = (*env)->FindClass(env,"org/libvirt/SchedLongParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "J"), params[c].value.l);
			break;
		case	VIR_DOMAIN_SCHED_FIELD_ULLONG:
			cls = (*env)->FindClass(env,"org/libvirt/SchedUlongParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "J"), params[c].value.ul);
			break;
		case	VIR_DOMAIN_SCHED_FIELD_DOUBLE:
			cls = (*env)->FindClass(env,"org/libvirt/SchedDoubleParameter");
			j_param=(*env)->AllocObject(env, cls);
			(*env)->SetObjectField(env, j_param, (*env)->GetFieldID(env, cls, "field", "Ljava/lang/String;"), j_field);
			(*env)->SetIntField(env, j_param, (*env)->GetFieldID(env, cls, "value", "D"), params[c].value.d);
			break;
		case    VIR_DOMAIN_SCHED_FIELD_BOOLEAN:
			cls = (*env)->FindClass(env,"org/libvirt/SchedBooleanParameter");
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

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1setSchedulerParameters
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
		const char *field;

		if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/SchedIntParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "I");
			field = (*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL);
			strcpy(params[c].field, field);
			(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), field);
			params[c].value.i= (*env)->GetIntField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_INT;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/SchedUintParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "I");
			field = (*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL);
			strcpy(params[c].field, field);
			(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), field);
			params[c].value.ui= (*env)->GetIntField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_UINT;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/SchedLongParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "J");
			field = (*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL);
			strcpy(params[c].field, field);
			(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), field);
			params[c].value.l= (*env)->GetLongField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_LLONG;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/SchedUlongParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "J");
			field = (*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL);
			strcpy(params[c].field, field);
			(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), field);
			params[c].value.ul= (*env)->GetLongField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_ULLONG;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/SchedDoubleParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "D");
			field = (*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL);
			strcpy(params[c].field, field);
			(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), field);
			params[c].value.d= (*env)->GetDoubleField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_ULLONG;
		} else if((*env)->IsInstanceOf(env, j_param, (*env)->FindClass(env, "org/libvirt/SchedBooleanParameter")))
		{
			field_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "field", "Ljava/lang/String;");
			value_id= (*env)->GetFieldID(env, (*env)->GetObjectClass(env, j_param), "value", "Z");
			field = (*env)->GetStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), NULL);
			strcpy(params[c].field, field);
			(*env)->ReleaseStringUTFChars(env, (*env)->GetObjectField(env, j_param, field_id), field);
			params[c].value.b= (*env)->GetBooleanField(env, j_param, value_id);
			params[c].type= VIR_DOMAIN_SCHED_FIELD_BOOLEAN;
		}
	}

	returnvalue= virDomainSetSchedulerParameters((virDomainPtr)VDP, params, nparams);
	free(params);

	return returnvalue;
}

JNIEXPORT jintArray JNICALL Java_org_libvirt_Domain__1getUUID
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC_GETUUID(env, obj, (virDomainPtr)VDP, virDomainGetUUID)
}

JNIEXPORT jstring JNICALL Java_org_libvirt_Domain__1getUUIDString
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC_GETUUIDSTRING(env, obj, (virDomainPtr)VDP, virDomainGetUUIDString)
}

JNIEXPORT jobjectArray JNICALL Java_org_libvirt_Domain__1getVcpusInfo
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
		vcpuinfo_class = (*env)->FindClass(env,"org/libvirt/VcpuInfo");
		number_id = (*env)->GetFieldID(env, vcpuinfo_class, "number", "I");
		state_id = (*env)->GetFieldID(env, vcpuinfo_class, "state", "Lorg/libvirt/VcpuInfo$VcpuState;");
		cputime_id =  (*env)->GetFieldID(env, vcpuinfo_class, "cputime", "J");
		cpu_id =  (*env)->GetFieldID(env, vcpuinfo_class, "cpu", "I");

		//Get objects for the states so that we can copy them into the info structure
		state_class=(*env)->FindClass(env,"org.libvirt.VcpuInfo$VcpuState");
		state_values_id=(*env)->GetStaticMethodID(env, state_class, "values", "()[Lorg/libvirt/VcpuInfo$VcpuState");
		state_values=(*env)->CallStaticObjectMethod(env, state_class, state_values_id);

		//We need a dummy element to initialize the array
		j_info = (*env)->NewObject(env,
				(*env)->FindClass(env,"org/libvirt/VcpuInfo"),
				(*env)->GetMethodID(env, (*env)->FindClass(env,"org/libvirt/VcpuInfo"), "<init>", "()V"));

		//Create the info array
		j_infoArray= (jobjectArray)(*env)->NewObjectArray(env, maxinfo,
				(*env)->FindClass(env,"org/libvirt/VcpuInfo"),
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

JNIEXPORT jintArray JNICALL Java_org_libvirt_Domain__1getVcpusCpuMaps
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

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1pinVcpu
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

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1setVcpus
  (JNIEnv *env, jobject obj, jlong VDP, jint nvcpus){
	GENERIC__VIROBJ_INT__INT(env, obj, (virDomainPtr)VDP, nvcpus, virDomainSetVcpus)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1attachDevice
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_xmlDesc){
	GENERIC_VIROBJ_STRING__INT(env, obj, (virDomainPtr)VDP, j_xmlDesc, virDomainAttachDevice);
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1detachDevice
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_xmlDesc){
	GENERIC_VIROBJ_STRING__INT(env, obj, (virDomainPtr)VDP, j_xmlDesc, virDomainDetachDevice);
}


JNIEXPORT jobject JNICALL Java_org_libvirt_Domain__1blockStats
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_path){
	struct  _virDomainBlockStats stats;
	jobject j_stats;
	jclass stats_cls=(*env)->FindClass(env, "org/libvirt/DomainInterfaceStats");
	const char *path = (*env)->GetStringUTFChars(env, j_path, NULL);

	if(virDomainBlockStats((virDomainPtr)VDP, path, &stats, sizeof(struct  _virDomainBlockStats))<0){
		(*env)->ReleaseStringUTFChars(env, j_path, path);
		return NULL;
	}
	(*env)->ReleaseStringUTFChars(env, j_path, path);

	j_stats = (*env)->AllocObject(env, stats_cls);

	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rd_req", "J"), stats.rd_req);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "rd_bytes", "J"), stats.rd_bytes);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "wr_req", "J"), stats.wr_req);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "wr_bytes", "J"), stats.wr_bytes);
	(*env)->SetLongField(env, j_stats, (*env)->GetFieldID(env, stats_cls, "errs", "J"), stats.errs);

	return j_stats;
}


JNIEXPORT jobject JNICALL Java_org_libvirt_Domain__1interfaceStats
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_path){
	struct  _virDomainInterfaceStats stats;
	jobject j_stats;
	jclass stats_cls=(*env)->FindClass(env, "org/libvirt/DomainInterfaceStats");
	const char *path = (*env)->GetStringUTFChars(env, j_path, NULL);

	if(virDomainInterfaceStats((virDomainPtr)VDP, (*env)->GetStringUTFChars(env, j_path, NULL), &stats, sizeof(struct  _virDomainInterfaceStats))<0){
		(*env)->ReleaseStringUTFChars(env, j_path, path);
		return NULL;
	}
	(*env)->ReleaseStringUTFChars(env, j_path, path);

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

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1coreDump
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_to, jint flags){
	const char *to = (*env)->GetStringUTFChars(env, j_to, NULL);
	jint retval = virDomainCoreDump((virDomainPtr)VDP, to, flags);
	(*env)->ReleaseStringUTFChars(env, j_to, to);
	return retval;
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1create
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainCreate)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1destroy
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainDestroy)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1free
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainFree)
}

JNIEXPORT jobject JNICALL Java_org_libvirt_Domain__1getInfo
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
	domaininfo_class = (*env)->FindClass(env,"org/libvirt/DomainInfo");

	state_id = (*env)->GetFieldID(env, domaininfo_class, "state", "Lorg/libvirt/DomainInfo$DomainState;");
	maxMem_id = (*env)->GetFieldID(env, domaininfo_class, "maxMem", "J");
	memory_id =  (*env)->GetFieldID(env, domaininfo_class, "memory", "J");
	nrVirtCpu_id =  (*env)->GetFieldID(env, domaininfo_class, "nrVirtCpu", "I");
	cpuTime_id = (*env)->GetFieldID(env, domaininfo_class, "cpuTime", "J");

	//Get objects for the states so that we can copy them into the info structure
	state_class=(*env)->FindClass(env,"org/libvirt/DomainInfo$DomainState");
	state_values_id=(*env)->GetStaticMethodID(env, state_class, "values", "()[Lorg/libvirt/DomainInfo$DomainState;");
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

JNIEXPORT jlong JNICALL Java_org_libvirt_Domain__1migrate
  (JNIEnv *env, jobject obj, jlong VDP, jobject dconn, jlong flags, jstring j_dname, jstring j_uri, jlong bandwidth){

	virConnectPtr destVCP;

	const char *dname=NULL;
	const char *uri=NULL;

	//if String="", we pass NULL to the library
	if((*env)->GetStringLength(env, j_dname)>0)
		dname=(*env)->GetStringUTFChars(env, j_dname, NULL);

	//if String="", we pass NULL to the library
	if((*env)->GetStringLength(env, j_uri)>0)
		uri=(*env)->GetStringUTFChars(env, j_uri, NULL);

	//Extract the destination Conn Ptr
	destVCP=(virConnectPtr)(*env)->GetLongField(env, dconn,
			(*env)->GetFieldID(env, (*env)->GetObjectClass(env, dconn), "VCP", "J"));

	jlong retval = (jlong)virDomainMigrate((virDomainPtr)VDP, destVCP, flags, dname, uri, bandwidth);
	(*env)->ReleaseStringUTFChars(env, j_dname, dname);
	(*env)->ReleaseStringUTFChars(env, j_uri, uri);
	return retval;
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1reboot
  (JNIEnv *env, jobject obj, jlong VDP, jint flags){
	GENERIC__VIROBJ_INT__INT(env, obj, (virDomainPtr)VDP, flags, virDomainReboot)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1suspend
(JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainSuspend)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1resume
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainResume)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1save
  (JNIEnv *env, jobject obj, jlong VDP, jstring j_to){
	GENERIC_VIROBJ_STRING__INT(env, obj, (virDomainPtr)VDP, j_to, virDomainSave)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1shutdown
  (JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainShutdown)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1undefine
(JNIEnv *env, jobject obj, jlong VDP){
	GENERIC__VIROBJ__INT(env, obj, (virDomainPtr)VDP, virDomainUndefine)
}

JNIEXPORT jint JNICALL Java_org_libvirt_Domain__1setMemory
  (JNIEnv *env, jobject obj, jlong VDP, jlong memory){
	return virDomainSetMemory((virDomainPtr)VDP, memory);
}

