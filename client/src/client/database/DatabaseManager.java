package client.database;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.content.Context;

public class DatabaseManager {
	
	private static volatile DatabaseManager instance;
	
	public static DatabaseManager get(Context context){
		if (context == null) return null;
		if (instance == null ) instance = new DatabaseManager(context);
		return instance;
	}

	private DatabaseHelper helper;
	
	private DatabaseManager(Context context) {
		helper = new DatabaseHelper(context);
	}
	
	private DatabaseHelper getHelper() {
		return helper;
	}
	
	private PreparedQuery<Object> getPreparedQuery(Dao <Object, Integer> dao, Object[] request){
		if (dao == null || request == null || request.length % 2 != 0) return null;
		QueryBuilder<Object, Integer> queryBuilder = dao.queryBuilder();
		Where<Object, Integer> where = queryBuilder.where();
		where = getWhere(where, request);
		if (where == null) return null;
		PreparedQuery<Object> preparedQuery = null;
		try {
			preparedQuery = queryBuilder.prepare();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return preparedQuery;
	}
	
	private Where<Object, Integer> getWhere(Where<Object, Integer> where, Object[] request){
		if (where == null || request == null || request.length % 2 != 0) return null;
		int cycles = request.length > 2 ? request.length/2: 1;
		for (int i = 0; i < cycles; i++){//2
			try {
				where = where.eq(request[i*2].toString(), request[i*2+1]);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			if (i < cycles - 1) where = where.and();
		}
		return where;
	}
	
	public void deleteObject (Object object){
		if (object == null) return;
		Dao<Object, Integer> dao = null;
		try {
			dao = getDAO(object.getClass());
			if (dao == null) return;
			dao.delete(object);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createObject (Object object){
		if (object == null) return;
		Dao<Object, Integer> dao = null;
		try {
			dao = getDAO(object.getClass());
			if (dao == null) return;
			dao.create(object);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	@SuppressWarnings("unchecked")
	private Dao<Object, Integer> getDAO(Class<?> clas){
		Dao<Object, Integer> dao = null;
		try {
			dao = (Dao <Object, Integer>) getHelper().getDao(clas);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dao;
	}

	public void updateObject (Object object){
		if (object == null) return;
		Dao<Object, Integer> dao = null;
		try {
			dao = getDAO(object.getClass());
			if (dao == null) return;
			dao.update(object);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List <Object> getList(Class<?> clas, Object[] request){
		if (request == null || request.length % 2 != 0) return null;
		Dao<Object, Integer> dao = getDAO(clas);
		if (dao == null) return null;
		List<Object> list = null;
		PreparedQuery<Object> mPreparedQuery = getPreparedQuery(dao, request);
		if (mPreparedQuery == null) return null;
		try {
			list = dao.query(mPreparedQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * @param clas
	 * @param request Object[] {String FIELD_NAME_1, Object value1, String FIELD_NAME_2, Object value2, ...}
	 * @return
	 */
	public Object getFirst(Class<?> clas, Object[] request){
		if (request == null || request.length % 2 != 0) return null;
		Dao<Object, Integer> dao = getDAO(clas);
		if (dao == null) return null;
		Object object = null;
		CloseableIterator<Object> mIterator = null;
		PreparedQuery<Object> mPreparedQuery = getPreparedQuery(dao, request);
		if (mPreparedQuery == null) return null;
		try {
			mIterator = dao.iterator(mPreparedQuery);
			if (mIterator.hasNext()) object = mIterator.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		     if (mIterator != null)
				try {
					mIterator.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		 }
		return object;
	}
	
	private List <Object> getList(Dao<Object, Integer> dao){
		if (dao == null) return null;
		List <Object> list = null;
		if (dao != null)
			try {
				list = dao.queryForAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return list;
	}
	
	public List <Object> getAll(Class<?> clas){
		Dao<Object, Integer> dao = getDAO(clas);
		if (dao == null) return null;
		return getList(dao);
	}
}