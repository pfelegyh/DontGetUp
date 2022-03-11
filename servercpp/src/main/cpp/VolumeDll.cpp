#include <windows.h>
#include <mmdeviceapi.h>
#include <endpointvolume.h>
#include "com_meandmyphone_server_services_impl_VolumeService.h"
#include "com_meandmyphone_server_services_impl_MouseService.h"
#include "com_meandmyphone_server_services_impl_KeyboardService.h"
#include <jni.h>
#include <iostream>
#include <vector>

#define DLLExport __declspec(dllexport)


extern "C"
{

	typedef struct _JNI_POSREC_SCREEN {
		jclass cls;
		jmethodID constructortorID;
		jfieldID indexId;
		jfieldID leftId;
		jfieldID topId;
		jfieldID rightId;
		jfieldID bottomId;
	} JNI_POSREC_SCREEN;

	typedef struct _JNI_POSREC_POINT {
		jclass cls;
		jmethodID constructortorID;
		jfieldID x;
		jfieldID y;
	} JNI_POSREC_POINT;


	struct Screen {
		int index;
		int left;
		int top;
		int right;
		int bottom;
	};


	void InitializeScreenData();
	BOOL CALLBACK MonitorEnumProc(HMONITOR hMonitor, HDC hdcMonitor, LPRECT lprcMonitor, LPARAM dwData);
	void LoadJniPosRecScreen(JNIEnv* env);
	void LoadJniPosRecPoint(JNIEnv* env);

	std::vector<Screen> screens;
	JNI_POSREC_SCREEN* jniPos_screen = NULL;
	JNI_POSREC_POINT* jniPos_point = NULL;

	jint previousVolume = -1;
	bool screenInitialized = false;

	JNIEXPORT jint JNICALL Java_com_meandmyphone_server_services_impl_VolumeService_getVolume(JNIEnv*, jobject) {
		HRESULT hr;
		CoInitialize(NULL);
		IMMDeviceEnumerator* deviceEnumerator = NULL;
		hr = CoCreateInstance(__uuidof(MMDeviceEnumerator), NULL, CLSCTX_INPROC_SERVER, __uuidof(IMMDeviceEnumerator), (LPVOID*)&deviceEnumerator);
		IMMDevice* defaultDevice = NULL;
		hr = deviceEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &defaultDevice);
		deviceEnumerator->Release();
		deviceEnumerator = NULL;
		IAudioEndpointVolume* endpointVolume = NULL;
		hr = defaultDevice->Activate(__uuidof(IAudioEndpointVolume), CLSCTX_INPROC_SERVER, NULL, (LPVOID*)&endpointVolume);
		defaultDevice->Release();
		defaultDevice = NULL;
		float currentVolume = 0;
		hr = endpointVolume->GetMasterVolumeLevelScalar(&currentVolume);
		endpointVolume->Release();
		CoUninitialize();
		jint retval = (jint)(65535 * currentVolume);
		if (previousVolume != retval) {
			std::cout << "Volume change detected, current volume = " << retval << std::endl;
			previousVolume = retval;
		}

		return retval;
	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_VolumeService_setVolume(JNIEnv*, jobject, jint volume) {
		HRESULT hr;

		CoInitialize(NULL);
		IMMDeviceEnumerator* deviceEnumerator = NULL;
		hr = CoCreateInstance(__uuidof(MMDeviceEnumerator), NULL, CLSCTX_INPROC_SERVER, __uuidof(IMMDeviceEnumerator), (LPVOID*)&deviceEnumerator);
		IMMDevice* defaultDevice = NULL;

		hr = deviceEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &defaultDevice);
		deviceEnumerator->Release();
		deviceEnumerator = NULL;

		IAudioEndpointVolume* endpointVolume = NULL;
		hr = defaultDevice->Activate(__uuidof(IAudioEndpointVolume), CLSCTX_INPROC_SERVER, NULL, (LPVOID*)&endpointVolume);
		defaultDevice->Release();
		defaultDevice = NULL;

		float volumeNormalized = volume / (float)65535.0;

		hr = endpointVolume->SetMasterVolumeLevelScalar((float)volumeNormalized, NULL);

		endpointVolume->Release();

		CoUninitialize();
	}

	JNIEXPORT jobject JNICALL Java_com_meandmyphone_server_services_impl_MouseService_getMousePosition(JNIEnv* env, jobject) {
		LPPOINT point = new POINT;
		GetCursorPos(point);
		int x = point->x;
		int y = point->y;

		LoadJniPosRecPoint(env);

		jobject jPos_point = env->NewObject(jniPos_point->cls, jniPos_point->constructortorID);

		jint jix = (jint)x;
		env->SetIntField(jPos_point, jniPos_point->x, jix);

		jint jiy = (jint)y;
		env->SetIntField(jPos_point, jniPos_point->y, jiy);

		return jPos_point;
	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_setMousePosition(JNIEnv*, jobject, jint x, jint y) {
		SetCursorPos(x, y);

	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_clickMouse(JNIEnv*, jobject, jint button) {
		if (button == 0)
			mouse_event(MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
		else if (button == 1) {
			mouse_event(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
		}
	}

	JNIEXPORT jobjectArray JNICALL Java_com_meandmyphone_server_services_impl_MouseService_getScreens(JNIEnv* env, jobject) {

		if (!screenInitialized) {
			InitializeScreenData();
		}

		LoadJniPosRecScreen(env);
		jobjectArray jPosRecArray = env->NewObjectArray(screens.size(), jniPos_screen->cls, NULL);


		for (int i = 0; i < screens.size(); ++i) {
			Screen element = screens[i];

			jobject jPos_screen = env->NewObject(jniPos_screen->cls, jniPos_screen->constructortorID);
			jint index = (jint)element.index;
			env->SetIntField(jPos_screen, jniPos_screen->indexId, index);
			jint left = (jint)element.left;
			env->SetIntField(jPos_screen, jniPos_screen->leftId, left);
			jint top = (jint)element.top;
			env->SetIntField(jPos_screen, jniPos_screen->topId, top);
			jint right = (jint)element.right;
			env->SetIntField(jPos_screen, jniPos_screen->rightId, right);
			jint bottom = (jint)element.bottom;
			env->SetIntField(jPos_screen, jniPos_screen->bottomId, bottom);

			env->SetObjectArrayElement(jPosRecArray, i, jPos_screen);

		}

		return jPosRecArray;
	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_holdMouseButton(JNIEnv*, jobject, jint button) {
		if (button == 0)
			mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
		else if (button == 1) {
			mouse_event(MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
		}
	}

	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_MouseService_releaseMouseButton(JNIEnv*, jobject, jint button) {
		if (button == 0)
			mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
		else if (button == 1) {
			mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
		}
	}


	JNIEXPORT void JNICALL Java_com_meandmyphone_server_services_impl_KeyboardService_inputKeys(JNIEnv* env, jobject, jstring input) {
		INPUT ip;

		ip.type = INPUT_KEYBOARD;
		ip.ki.wScan = 0; 
		ip.ki.time = 0;
		ip.ki.dwExtraInfo = 0;

		HKL locale = GetKeyboardLayout(0);

		

		const char* nativeString = env->GetStringUTFChars(input, 0);

		if (0 == strcmp(nativeString, "enterKeycode")) {
			ip.ki.wVk = VK_RETURN;
			ip.ki.dwFlags = 0; // 0 for key press

			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));
			return;
		}
		else if (0 == strcmp(nativeString, "backspaceKeycode")) {
			ip.ki.wVk = 0x08;
			ip.ki.dwFlags = 0; // 0 for key press

			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));

			ip.ki.wVk = 0x2E;
			ip.ki.dwFlags = 0; // 0 for key press
			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));


			return;
		}

		const size_t len = strlen(nativeString);
		char chars[100];

		strncpy_s(chars, nativeString, len);

		for (int i = 0; i < len; i++) {

			SHORT keyCode = VkKeyScanExA(chars[i], locale);
			bool shiftDown = false;
			bool ctrlDown = false;


			INPUT ctrlIp;
			INPUT shiftIp; 

			if (keyCode & 0x100) // check upper byte for shift flag
			{
				shiftDown = true;

				shiftIp.type = INPUT_KEYBOARD;
				shiftIp.ki.wScan = 0;
				shiftIp.ki.time = 0;
				shiftIp.ki.dwExtraInfo = 0;
				shiftIp.ki.wVk = 0xA0;
				shiftIp.ki.dwFlags = 0;
				SendInput(1, &shiftIp, sizeof(INPUT));
			}
			if (keyCode & 0x200) // check for ctrl flag
			{
				ctrlDown = true;

				ctrlIp.type = INPUT_KEYBOARD;
				ctrlIp.ki.wScan = 0;
				ctrlIp.ki.time = 0;
				ctrlIp.ki.dwExtraInfo = 0;
				ctrlIp.ki.wVk = 0xA2;
				ctrlIp.ki.dwFlags = 0;
				SendInput(1, &ctrlIp, sizeof(INPUT));
			}


			ip.ki.wVk = keyCode & 0xFF; 
			ip.ki.dwFlags = 0; // 0 for key press

			SendInput(1, &ip, sizeof(INPUT));
			ip.ki.dwFlags = KEYEVENTF_KEYUP; // KEYEVENTF_KEYUP for key release
			SendInput(1, &ip, sizeof(INPUT));

			if (shiftDown) {
				shiftIp.ki.dwFlags = KEYEVENTF_KEYUP;
				SendInput(1, &shiftIp, sizeof(INPUT));
			}

			if (ctrlDown) {
				ctrlIp.ki.dwFlags = KEYEVENTF_KEYUP;
				SendInput(1, &ctrlIp, sizeof(INPUT));
			}
		}

	}

	void LoadJniPosRecScreen(JNIEnv* env) {

		jniPos_screen = new JNI_POSREC_SCREEN;

		jniPos_screen->cls = env->FindClass("com/meandmyphone/server/vo/Screen");

		jniPos_screen->constructortorID = env->GetMethodID(jniPos_screen->cls, "<init>", "()V");
		jniPos_screen->indexId = env->GetFieldID(jniPos_screen->cls, "index", "I");
		jniPos_screen->leftId = env->GetFieldID(jniPos_screen->cls, "left", "I");
		jniPos_screen->topId = env->GetFieldID(jniPos_screen->cls, "top", "I");
		jniPos_screen->rightId = env->GetFieldID(jniPos_screen->cls, "right", "I");
		jniPos_screen->bottomId = env->GetFieldID(jniPos_screen->cls, "bottom", "I");

	}

	void LoadJniPosRecPoint(JNIEnv* env) {
		jniPos_point = new JNI_POSREC_POINT;

		jniPos_point->cls = env->FindClass("com/meandmyphone/server/vo/Point");
		jniPos_point->constructortorID = env->GetMethodID(jniPos_point->cls, "<init>", "()V");
		jniPos_point->x = env->GetFieldID(jniPos_point->cls, "x", "I");
		jniPos_point->y = env->GetFieldID(jniPos_point->cls, "y", "I");
	}


	void InitializeScreenData()
	{
		screens.clear();
		EnumDisplayMonitors(NULL, NULL, MonitorEnumProc, (LPARAM)NULL);

		std::cout << "---------------------" << std::endl;
		std::cout << "Initializing Screens" << std::endl;

		for (int i = 0; i < screens.size(); ++i) {
			Screen element = screens[i];
			std::cout << "---- Monitor " << screens[i].index << "---- " << std::endl;
			std::cout << "left" << screens[i].left << std::endl;
			std::cout << "top" << screens[i].top << std::endl;
			std::cout << "right" << screens[i].right << std::endl;
			std::cout << "bottom" << screens[i].bottom << std::endl;
		}

		std::cout << "Number of monitors: " << screens.size() << std::endl;
		std::cout << "---------------------" << std::endl;

	}


	BOOL CALLBACK MonitorEnumProc(HMONITOR hMonitor, HDC hdcMonitor, LPRECT lprcMonitor, LPARAM dwData)
	{

		MONITORINFO target;
		target.cbSize = sizeof(MONITORINFO);

		GetMonitorInfo(hMonitor, &target);

		Screen screen;
		screen.index = screens.size();
		screen.left = target.rcMonitor.left;
		screen.top = target.rcMonitor.top;
		screen.right = target.rcMonitor.right;
		screen.bottom = target.rcMonitor.bottom;

		screens.push_back(screen);

		return TRUE;
	}

}