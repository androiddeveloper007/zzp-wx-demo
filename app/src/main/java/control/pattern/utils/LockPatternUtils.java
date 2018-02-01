package control.pattern.utils;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 图案解锁加密、解密工具类
 * 
 * @author way
 * 
 */
public class LockPatternUtils {
	private static final String TAG = "LockPatternUtils";
	private static final String LOCK_PATTERN_FILE = "gesture.key";
	/**
	 * The minimum number of dots in a valid pattern.
	 */
	public static final int MIN_LOCK_PATTERN_SIZE = 4;
	
	/**
	 * The maximum number of incorrect attempts before the user is prevented
	 * from trying again for {@link #FAILED_ATTEMPT_TIMEOUT_MS}.
	 */
	public static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 5;
	
	/**
	 * The minimum number of dots the user must include in a wrong pattern
	 * attempt for it to be counted against the counts that affect
	 * {@link #FAILED_ATTEMPTS_BEFORE_TIMEOUT}and
	 *
	 */
	public static final int MIN_PATTERN_REGISTER_FAIL = MIN_LOCK_PATTERN_SIZE;
	
	/**
	 * How long the user is prevented from trying again after entering the wrong
	 * pattern too many times.
	 */
	public static final long FAILED_ATTEMPT_TIMEOUT_MS = 32000L;

	private File sLockPatternFilename;

	private static final AtomicBoolean sHaveNonZeroPatternFile = new AtomicBoolean(false);
	
	private static FileObserver sPasswordObserver;

	Context context;
	private class LockPatternFileObserver extends FileObserver {
		public LockPatternFileObserver(String path, int mask) {
			super(path, mask);
		}

		@Override
		public void onEvent(int event, String path) {
			Log.d(TAG, "file path" + path);
			if (LOCK_PATTERN_FILE.equals(path)) {
				Log.d(TAG, "lock pattern file changed");
				sHaveNonZeroPatternFile.set(sLockPatternFilename.length() > 0);
			}
		}
	}

	public LockPatternUtils(Context context,String phone) {
		if (sLockPatternFilename == null) {
			String dataSystemDirectory = context.getFilesDir().getAbsolutePath();
			
			sLockPatternFilename = new File(dataSystemDirectory, phone+".key");//LOCK_PATTERN_FILE
			sHaveNonZeroPatternFile.set(sLockPatternFilename.length() > 0);
			
			int fileObserverMask = FileObserver.CLOSE_WRITE
					| FileObserver.DELETE | FileObserver.MOVED_TO
					| FileObserver.CREATE;

			sPasswordObserver = new LockPatternFileObserver(dataSystemDirectory, fileObserverMask);
			sPasswordObserver.startWatching();

			this.context = context;
		}
	}

	/**
	 * Check to see if the user has stored a lock pattern.
	 *
	 * @return Whether a saved pattern exists.
	 */
	public boolean savedPatternExists() {
		return sHaveNonZeroPatternFile.get();
	}

	public void clearLock() {
		saveLockPattern(null);
	}

	/**
	 * Deserialize a pattern. 解密,用于保存状
	 *
	 * @param string
	 *            The pattern serialized with {@link #patternToString}
	 * @return The pattern.
	 */
	public static List<LockPatternView.Cell> stringToPattern(String string) {
		List<LockPatternView.Cell> result = new ArrayList<LockPatternView.Cell>();

		final byte[] bytes = string.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			result.add(LockPatternView.Cell.of(b / 3, b % 3));
		}
		return result;
	}

	/**
	 * Serialize a pattern. 加密
	 * 
	 * @param pattern
	 *            The pattern.
	 * @return The pattern in string form.
	 */
	public static String patternToString(List<LockPatternView.Cell> pattern) {
		if (pattern == null) {
			return "";
		}
		final int patternSize = pattern.size();

		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++) {
			LockPatternView.Cell cell = pattern.get(i);
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}
		return new String(res);
	}

	/**
	 * Save a lock pattern.
	 * 
	 * @param pattern
	 *            The new pattern to save.
	 */
	public void saveLockPattern(List<LockPatternView.Cell> pattern) {
			final byte[] hash = LockPatternUtils.patternToHash(pattern);
			try {
				// Write the hash to file
				RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "rwd");
				// Truncate the file if pattern is null, to clear the lock
				if (pattern == null) {
					raf.setLength(0);
				} else {
					raf.write(hash, 0, hash.length);
				}
				raf.close();
			} catch (FileNotFoundException fnfe) {
				Log.e("LockPatternUtil","Unable to save lock pattern to \" + sLockPatternFilename FileNotFoundException");
			} catch (IOException ioe) {
				Log.e("LockPatternUtil","Unable to save lock pattern to \" + sLockPatternFilename IOException");
			}
	}

	/*
	 * 把图形解锁密码根据位置转为0,1,2,4,5,6,7,8对应的8个数字，然后把数据列表散列（SHA-1）返回散列后的集合
	 * @param 手势位置列表
	 * @return 转换后手势列表散列后的集合
	 */
	private static byte[] patternToHash(List<LockPatternView.Cell> pattern) {
		if (pattern == null) {
			return null;
		}

		final int patternSize = pattern.size();
		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++) {
			LockPatternView.Cell cell = pattern.get(i);
			// 通过这种方式把Cell所在的位置转成 9个点分别的位置为0,1,2,3,4,5,6,7,8
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}
		
		
//		try {
//			MessageDigest md = MessageDigest.getInstance("SHA-1");
//			byte[] hash = md.digest(res);
//			return hash;
//		} catch (NoSuchAlgorithmException nsa) {
//		}
		return res;
	}

	/**
	 * 检查密码是否和保存的密码匹配，如果没保存密码则总是返回true
	 * 
	 * @param pattern 要检查的密码
	 * @return 如果保存了密码，匹配返回true，如果没有保存密码返回true
	 */
	public boolean checkPattern(List<LockPatternView.Cell> pattern) {
		try {
			// 从文件中读取保存的密码sLockPatternFilename
			RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "r");
			final byte[] stored = new byte[(int) raf.length()];
			int got = raf.read(stored, 0, stored.length);
			raf.close();

			if (got <= 0) {
				return true;
			}

			// 比较密码是否匹配
			return Arrays.equals(stored, LockPatternUtils.patternToHash(pattern));
		} catch (FileNotFoundException fnfe) {
			Log.e("LockPatternUtil","FileNotFoundException");
			return true;
		} catch (IOException ioe) {
			Log.e("LockPatternUtil","IOException");
			return true;
		}
	}
}
