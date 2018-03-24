package mp3ready.download.services;

interface IDownloadService {
	
	void startManage();
	
	void addTask(String url,String title);
	
	void pauseTask(String url);
	
	void deleteTask(String url);
	
	void continueTask(String url);
}
