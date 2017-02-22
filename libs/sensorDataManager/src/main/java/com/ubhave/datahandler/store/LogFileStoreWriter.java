package com.ubhave.datahandler.store;

import android.os.Environment;
import android.util.Log;

import com.ubhave.datahandler.config.DataHandlerConfig;
import com.ubhave.datahandler.config.DataStorageConfig;
import com.ubhave.datahandler.config.DataStorageConstants;
import com.ubhave.datahandler.except.DataHandlerException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class LogFileStoreWriter
{
	private static final String TAG = "LogFileDataStorage";

	private final FileStoreCleaner fileStoreCleaner;
	private final DataHandlerConfig config;
	private final HashMap<String, Object> lockMap;

	public LogFileStoreWriter(final FileStoreCleaner fileStoreCleaner, final HashMap<String, Object> lockMap)
	{
		this.config = DataHandlerConfig.getInstance();
		this.fileStoreCleaner = fileStoreCleaner;
		this.lockMap = lockMap;
	}

	public void writeData(String directoryName, String data) throws DataHandlerException
	{
		String rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//		if (rootPath.contains(DataStorageConfig.DEFAULT_ROOT_DIRECTORY))
//		{
//			throw new DataHandlerException(DataHandlerException.WRITING_TO_DEFAULT_DIRECTORY);
//		}

		synchronized (getLock(directoryName))
		{
			try
			{
				File directory = getDirectory(rootPath, directoryName);
				File dataFile = getLastEditedFile(directory);
				if (dataFile == null || !dataFile.exists())
				{
					// Creating date specific timestamp based file
					long currentTime = System.currentTimeMillis();
					Calendar fileCal = Calendar.getInstance();
					fileCal.setTimeInMillis(currentTime);
					fileCal.set(Calendar.HOUR, 0);
					fileCal.set(Calendar.HOUR_OF_DAY, 0);
					fileCal.set(Calendar.MINUTE, 0);
					fileCal.set(Calendar.SECOND, 0);
					fileCal.set(Calendar.MILLISECOND, 0);

					SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy", Locale.US);
					String dateStr = formatter.format(fileCal.getTime());
					String userId = (String) config.get(DataStorageConfig.UNIQUE_USER_ID);

					dataFile = new File(directory.getAbsolutePath() + "/" + userId + "_sensors_" + dateStr);
					if (DataHandlerConfig.shouldLog())
					{
						Log.d(TAG, "Creating: " + dataFile.getAbsolutePath());
					}

					boolean fileCreated = dataFile.createNewFile();
					if (!fileCreated && DataHandlerConfig.shouldLog())
					{
						Log.d(TAG, "Creating file returned false");
					}
				}

				// Append data to file
				FileOutputStream fos = new FileOutputStream(dataFile, true);
				fos.write(data.getBytes());
				fos.write("\n".getBytes());
				fos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new DataHandlerException(DataHandlerException.IO_EXCEPTION);
			}
		}
	}

	private Object getLock(final String key)
	{
		Object lock;
		synchronized (lockMap)
		{
			if (lockMap.containsKey(key))
			{
				lock = lockMap.get(key);
			}
			else
			{
				lock = new Object();
				lockMap.put(key, lock);
			}
		}
		return lock;
	}

	private File getDirectory(final String rootPath, final String directoryName)
	{
		File directory = new File(rootPath, directoryName);
		if (!directory.exists())
		{
			if (DataHandlerConfig.shouldLog())
			{
				Log.d(TAG, "Creating: " + directory.getAbsolutePath());
			}
			directory.mkdirs();
		}
		return directory;
	}

	private File getLastEditedFile(final File directory) throws DataHandlerException, IOException
	{
		long latestUpdate = Long.MIN_VALUE;
		File latestFile = null;
		File[] files = directory.listFiles();
		if (files != null)
		{
			for (File file : files)
			{
				if (file.isFile() && file.getName().contains("_sensors_"))
				{
					long update = file.lastModified();
					if (update > latestUpdate)
					{
						latestUpdate = update;
						latestFile = file;
					}
				}
			}
		}
		if (latestFile != null)
		{
			if (isFileLimitReached(latestFile))
			{
				fileStoreCleaner.moveDirectoryContentsForUpload(directory.getAbsolutePath());
				latestFile = null;
			}
		}
		return latestFile;
	}

	private long getDurationLimit()
	{
		try
		{
			return (Long) config.get(DataStorageConfig.FILE_LIFE_MILLIS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return DataStorageConfig.DEFAULT_FILE_LIFE_MILLIS;
		}
	}

	private boolean isFileLimitReached(File file)
	{
		try
		{
			if (file != null)
			{
				String fileName = file.getName();
				if (fileName != null)
				{
					if (fileName.contains(DataStorageConstants.LOG_FILE_SUFFIX))
					{
						Calendar curCal = Calendar.getInstance();
						curCal.setTimeInMillis(System.currentTimeMillis());
						SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy", Locale.US);

						String timeStr = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.indexOf(DataStorageConstants.LOG_FILE_SUFFIX));
						String curStr = formatter.format(curCal.getTime());

						if (timeStr.equalsIgnoreCase(curStr) == false)
						{
							return true;
						}
					}
				}
			}
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
