package com.taskplanner.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.taskplanner.app.data.model.EventType;
import com.taskplanner.app.data.model.NotificationFrequency;
import com.taskplanner.app.data.model.SyncedEvent;
import com.taskplanner.app.data.model.TaskItem;
import com.taskplanner.app.data.model.TaskPriority;
import com.taskplanner.app.data.model.TaskSource;
import com.taskplanner.app.data.model.TaskStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlannerDao_Impl implements PlannerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TaskItem> __insertionAdapterOfTaskItem;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<SyncedEvent> __insertionAdapterOfSyncedEvent;

  private final EntityDeletionOrUpdateAdapter<TaskItem> __deletionAdapterOfTaskItem;

  private final EntityDeletionOrUpdateAdapter<TaskItem> __updateAdapterOfTaskItem;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTaskById;

  private final SharedSQLiteStatement __preparedStmtOfMarkReminderScheduled;

  public PlannerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTaskItem = new EntityInsertionAdapter<TaskItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tasks` (`id`,`title`,`description`,`scheduledHour`,`priority`,`status`,`isCompleted`,`source`,`notificationFrequency`,`createdAt`,`completedAt`,`endDateMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TaskItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getScheduledHour());
        final String _tmp = __converters.fromTaskPriority(entity.getPriority());
        statement.bindString(5, _tmp);
        final String _tmp_1 = __converters.fromTaskStatus(entity.getStatus());
        statement.bindString(6, _tmp_1);
        final int _tmp_2 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final String _tmp_3 = __converters.fromTaskSource(entity.getSource());
        statement.bindString(8, _tmp_3);
        final String _tmp_4 = __converters.fromNotificationFrequency(entity.getNotificationFrequency());
        statement.bindString(9, _tmp_4);
        statement.bindLong(10, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getCompletedAt());
        }
        if (entity.getEndDateMillis() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getEndDateMillis());
        }
      }
    };
    this.__insertionAdapterOfSyncedEvent = new EntityInsertionAdapter<SyncedEvent>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `synced_events` (`eventId`,`title`,`eventType`,`startTimeMillis`,`endTimeMillis`,`meetingLink`,`reminderScheduled`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncedEvent entity) {
        statement.bindString(1, entity.getEventId());
        statement.bindString(2, entity.getTitle());
        final String _tmp = __converters.fromEventType(entity.getEventType());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getStartTimeMillis());
        if (entity.getEndTimeMillis() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getEndTimeMillis());
        }
        if (entity.getMeetingLink() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMeetingLink());
        }
        final int _tmp_1 = entity.getReminderScheduled() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
      }
    };
    this.__deletionAdapterOfTaskItem = new EntityDeletionOrUpdateAdapter<TaskItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tasks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TaskItem entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTaskItem = new EntityDeletionOrUpdateAdapter<TaskItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tasks` SET `id` = ?,`title` = ?,`description` = ?,`scheduledHour` = ?,`priority` = ?,`status` = ?,`isCompleted` = ?,`source` = ?,`notificationFrequency` = ?,`createdAt` = ?,`completedAt` = ?,`endDateMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TaskItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getScheduledHour());
        final String _tmp = __converters.fromTaskPriority(entity.getPriority());
        statement.bindString(5, _tmp);
        final String _tmp_1 = __converters.fromTaskStatus(entity.getStatus());
        statement.bindString(6, _tmp_1);
        final int _tmp_2 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final String _tmp_3 = __converters.fromTaskSource(entity.getSource());
        statement.bindString(8, _tmp_3);
        final String _tmp_4 = __converters.fromNotificationFrequency(entity.getNotificationFrequency());
        statement.bindString(9, _tmp_4);
        statement.bindLong(10, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getCompletedAt());
        }
        if (entity.getEndDateMillis() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getEndDateMillis());
        }
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteTaskById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tasks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkReminderScheduled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE synced_events SET reminderScheduled = 1 WHERE eventId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTask(final TaskItem task, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTaskItem.insertAndReturnId(task);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertTasks(final List<TaskItem> tasks,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfTaskItem.insertAndReturnIdsList(tasks);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEvent(final SyncedEvent event, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSyncedEvent.insertAndReturnId(event);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTask(final TaskItem task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTaskItem.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTask(final TaskItem task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTaskItem.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTaskById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTaskById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteTaskById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markReminderScheduled(final String id,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkReminderScheduled.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkReminderScheduled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TaskItem>> getTasksForHourFlow(final int hour) {
    final String _sql = "SELECT * FROM tasks WHERE scheduledHour = ? ORDER BY status ASC, priority ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, hour);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskItem>>() {
      @Override
      @NonNull
      public List<TaskItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledHour = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledHour");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotificationFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationFrequency");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfEndDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateMillis");
          final List<TaskItem> _result = new ArrayList<TaskItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpScheduledHour;
            _tmpScheduledHour = _cursor.getInt(_cursorIndexOfScheduledHour);
            final TaskPriority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toTaskPriority(_tmp);
            final TaskStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTaskStatus(_tmp_1);
            final boolean _tmpIsCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_2 != 0;
            final TaskSource _tmpSource;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTaskSource(_tmp_3);
            final NotificationFrequency _tmpNotificationFrequency;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfNotificationFrequency);
            _tmpNotificationFrequency = __converters.toNotificationFrequency(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpEndDateMillis;
            if (_cursor.isNull(_cursorIndexOfEndDateMillis)) {
              _tmpEndDateMillis = null;
            } else {
              _tmpEndDateMillis = _cursor.getLong(_cursorIndexOfEndDateMillis);
            }
            _item = new TaskItem(_tmpId,_tmpTitle,_tmpDescription,_tmpScheduledHour,_tmpPriority,_tmpStatus,_tmpIsCompleted,_tmpSource,_tmpNotificationFrequency,_tmpCreatedAt,_tmpCompletedAt,_tmpEndDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPendingTasksForHour(final int hour,
      final Continuation<? super List<TaskItem>> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE scheduledHour = ? AND status != 'COMPLETED' AND status != 'CANCELLED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, hour);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TaskItem>>() {
      @Override
      @NonNull
      public List<TaskItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledHour = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledHour");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotificationFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationFrequency");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfEndDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateMillis");
          final List<TaskItem> _result = new ArrayList<TaskItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpScheduledHour;
            _tmpScheduledHour = _cursor.getInt(_cursorIndexOfScheduledHour);
            final TaskPriority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toTaskPriority(_tmp);
            final TaskStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTaskStatus(_tmp_1);
            final boolean _tmpIsCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_2 != 0;
            final TaskSource _tmpSource;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTaskSource(_tmp_3);
            final NotificationFrequency _tmpNotificationFrequency;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfNotificationFrequency);
            _tmpNotificationFrequency = __converters.toNotificationFrequency(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpEndDateMillis;
            if (_cursor.isNull(_cursorIndexOfEndDateMillis)) {
              _tmpEndDateMillis = null;
            } else {
              _tmpEndDateMillis = _cursor.getLong(_cursorIndexOfEndDateMillis);
            }
            _item = new TaskItem(_tmpId,_tmpTitle,_tmpDescription,_tmpScheduledHour,_tmpPriority,_tmpStatus,_tmpIsCompleted,_tmpSource,_tmpNotificationFrequency,_tmpCreatedAt,_tmpCompletedAt,_tmpEndDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllPendingTasks(final Continuation<? super List<TaskItem>> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE status != 'COMPLETED' AND status != 'CANCELLED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TaskItem>>() {
      @Override
      @NonNull
      public List<TaskItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledHour = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledHour");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotificationFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationFrequency");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfEndDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateMillis");
          final List<TaskItem> _result = new ArrayList<TaskItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpScheduledHour;
            _tmpScheduledHour = _cursor.getInt(_cursorIndexOfScheduledHour);
            final TaskPriority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toTaskPriority(_tmp);
            final TaskStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTaskStatus(_tmp_1);
            final boolean _tmpIsCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_2 != 0;
            final TaskSource _tmpSource;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTaskSource(_tmp_3);
            final NotificationFrequency _tmpNotificationFrequency;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfNotificationFrequency);
            _tmpNotificationFrequency = __converters.toNotificationFrequency(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpEndDateMillis;
            if (_cursor.isNull(_cursorIndexOfEndDateMillis)) {
              _tmpEndDateMillis = null;
            } else {
              _tmpEndDateMillis = _cursor.getLong(_cursorIndexOfEndDateMillis);
            }
            _item = new TaskItem(_tmpId,_tmpTitle,_tmpDescription,_tmpScheduledHour,_tmpPriority,_tmpStatus,_tmpIsCompleted,_tmpSource,_tmpNotificationFrequency,_tmpCreatedAt,_tmpCompletedAt,_tmpEndDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TaskItem>> getAllTasksFlow() {
    final String _sql = "SELECT * FROM tasks ORDER BY scheduledHour ASC, priority ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskItem>>() {
      @Override
      @NonNull
      public List<TaskItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledHour = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledHour");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotificationFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationFrequency");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfEndDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateMillis");
          final List<TaskItem> _result = new ArrayList<TaskItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpScheduledHour;
            _tmpScheduledHour = _cursor.getInt(_cursorIndexOfScheduledHour);
            final TaskPriority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toTaskPriority(_tmp);
            final TaskStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTaskStatus(_tmp_1);
            final boolean _tmpIsCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_2 != 0;
            final TaskSource _tmpSource;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTaskSource(_tmp_3);
            final NotificationFrequency _tmpNotificationFrequency;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfNotificationFrequency);
            _tmpNotificationFrequency = __converters.toNotificationFrequency(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpEndDateMillis;
            if (_cursor.isNull(_cursorIndexOfEndDateMillis)) {
              _tmpEndDateMillis = null;
            } else {
              _tmpEndDateMillis = _cursor.getLong(_cursorIndexOfEndDateMillis);
            }
            _item = new TaskItem(_tmpId,_tmpTitle,_tmpDescription,_tmpScheduledHour,_tmpPriority,_tmpStatus,_tmpIsCompleted,_tmpSource,_tmpNotificationFrequency,_tmpCreatedAt,_tmpCompletedAt,_tmpEndDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<TaskItem>> getCompletedTasksFlow() {
    final String _sql = "SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY completedAt DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tasks"}, new Callable<List<TaskItem>>() {
      @Override
      @NonNull
      public List<TaskItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledHour = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledHour");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotificationFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationFrequency");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfEndDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateMillis");
          final List<TaskItem> _result = new ArrayList<TaskItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TaskItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpScheduledHour;
            _tmpScheduledHour = _cursor.getInt(_cursorIndexOfScheduledHour);
            final TaskPriority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toTaskPriority(_tmp);
            final TaskStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTaskStatus(_tmp_1);
            final boolean _tmpIsCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_2 != 0;
            final TaskSource _tmpSource;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTaskSource(_tmp_3);
            final NotificationFrequency _tmpNotificationFrequency;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfNotificationFrequency);
            _tmpNotificationFrequency = __converters.toNotificationFrequency(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpEndDateMillis;
            if (_cursor.isNull(_cursorIndexOfEndDateMillis)) {
              _tmpEndDateMillis = null;
            } else {
              _tmpEndDateMillis = _cursor.getLong(_cursorIndexOfEndDateMillis);
            }
            _item = new TaskItem(_tmpId,_tmpTitle,_tmpDescription,_tmpScheduledHour,_tmpPriority,_tmpStatus,_tmpIsCompleted,_tmpSource,_tmpNotificationFrequency,_tmpCreatedAt,_tmpCompletedAt,_tmpEndDateMillis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTaskById(final long id, final Continuation<? super TaskItem> $completion) {
    final String _sql = "SELECT * FROM tasks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TaskItem>() {
      @Override
      @Nullable
      public TaskItem call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledHour = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledHour");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfNotificationFrequency = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationFrequency");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfEndDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateMillis");
          final TaskItem _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpScheduledHour;
            _tmpScheduledHour = _cursor.getInt(_cursorIndexOfScheduledHour);
            final TaskPriority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toTaskPriority(_tmp);
            final TaskStatus _tmpStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toTaskStatus(_tmp_1);
            final boolean _tmpIsCompleted;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_2 != 0;
            final TaskSource _tmpSource;
            final String _tmp_3;
            _tmp_3 = _cursor.getString(_cursorIndexOfSource);
            _tmpSource = __converters.toTaskSource(_tmp_3);
            final NotificationFrequency _tmpNotificationFrequency;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfNotificationFrequency);
            _tmpNotificationFrequency = __converters.toNotificationFrequency(_tmp_4);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final Long _tmpEndDateMillis;
            if (_cursor.isNull(_cursorIndexOfEndDateMillis)) {
              _tmpEndDateMillis = null;
            } else {
              _tmpEndDateMillis = _cursor.getLong(_cursorIndexOfEndDateMillis);
            }
            _result = new TaskItem(_tmpId,_tmpTitle,_tmpDescription,_tmpScheduledHour,_tmpPriority,_tmpStatus,_tmpIsCompleted,_tmpSource,_tmpNotificationFrequency,_tmpCreatedAt,_tmpCompletedAt,_tmpEndDateMillis);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SyncedEvent>> getUpcomingEventsFlow(final long fromTime) {
    final String _sql = "SELECT * FROM synced_events WHERE startTimeMillis >= ? ORDER BY startTimeMillis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fromTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"synced_events"}, new Callable<List<SyncedEvent>>() {
      @Override
      @NonNull
      public List<SyncedEvent> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "eventId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "eventType");
          final int _cursorIndexOfStartTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimeMillis");
          final int _cursorIndexOfEndTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimeMillis");
          final int _cursorIndexOfMeetingLink = CursorUtil.getColumnIndexOrThrow(_cursor, "meetingLink");
          final int _cursorIndexOfReminderScheduled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderScheduled");
          final List<SyncedEvent> _result = new ArrayList<SyncedEvent>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncedEvent _item;
            final String _tmpEventId;
            _tmpEventId = _cursor.getString(_cursorIndexOfEventId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final EventType _tmpEventType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfEventType);
            _tmpEventType = __converters.toEventType(_tmp);
            final long _tmpStartTimeMillis;
            _tmpStartTimeMillis = _cursor.getLong(_cursorIndexOfStartTimeMillis);
            final Long _tmpEndTimeMillis;
            if (_cursor.isNull(_cursorIndexOfEndTimeMillis)) {
              _tmpEndTimeMillis = null;
            } else {
              _tmpEndTimeMillis = _cursor.getLong(_cursorIndexOfEndTimeMillis);
            }
            final String _tmpMeetingLink;
            if (_cursor.isNull(_cursorIndexOfMeetingLink)) {
              _tmpMeetingLink = null;
            } else {
              _tmpMeetingLink = _cursor.getString(_cursorIndexOfMeetingLink);
            }
            final boolean _tmpReminderScheduled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderScheduled);
            _tmpReminderScheduled = _tmp_1 != 0;
            _item = new SyncedEvent(_tmpEventId,_tmpTitle,_tmpEventType,_tmpStartTimeMillis,_tmpEndTimeMillis,_tmpMeetingLink,_tmpReminderScheduled);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getUnscheduledUpcomingEvents(final long now,
      final Continuation<? super List<SyncedEvent>> $completion) {
    final String _sql = "SELECT * FROM synced_events WHERE startTimeMillis > ? AND reminderScheduled = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncedEvent>>() {
      @Override
      @NonNull
      public List<SyncedEvent> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "eventId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfEventType = CursorUtil.getColumnIndexOrThrow(_cursor, "eventType");
          final int _cursorIndexOfStartTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "startTimeMillis");
          final int _cursorIndexOfEndTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "endTimeMillis");
          final int _cursorIndexOfMeetingLink = CursorUtil.getColumnIndexOrThrow(_cursor, "meetingLink");
          final int _cursorIndexOfReminderScheduled = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderScheduled");
          final List<SyncedEvent> _result = new ArrayList<SyncedEvent>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncedEvent _item;
            final String _tmpEventId;
            _tmpEventId = _cursor.getString(_cursorIndexOfEventId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final EventType _tmpEventType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfEventType);
            _tmpEventType = __converters.toEventType(_tmp);
            final long _tmpStartTimeMillis;
            _tmpStartTimeMillis = _cursor.getLong(_cursorIndexOfStartTimeMillis);
            final Long _tmpEndTimeMillis;
            if (_cursor.isNull(_cursorIndexOfEndTimeMillis)) {
              _tmpEndTimeMillis = null;
            } else {
              _tmpEndTimeMillis = _cursor.getLong(_cursorIndexOfEndTimeMillis);
            }
            final String _tmpMeetingLink;
            if (_cursor.isNull(_cursorIndexOfMeetingLink)) {
              _tmpMeetingLink = null;
            } else {
              _tmpMeetingLink = _cursor.getString(_cursorIndexOfMeetingLink);
            }
            final boolean _tmpReminderScheduled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfReminderScheduled);
            _tmpReminderScheduled = _tmp_1 != 0;
            _item = new SyncedEvent(_tmpEventId,_tmpTitle,_tmpEventType,_tmpStartTimeMillis,_tmpEndTimeMillis,_tmpMeetingLink,_tmpReminderScheduled);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
