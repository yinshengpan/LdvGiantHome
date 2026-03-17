package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ledvance.database.model.SetTripCurrentHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 17:26
 * Describe : SetTripCurrentHistoryDao
 */
@Dao
interface SetTripCurrentHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: SetTripCurrentHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(devices: List<SetTripCurrentHistoryEntity>)

    @Query("select * from set_trip_current_history order by create_time desc")
    fun getHistoryListFlow(): Flow<List<SetTripCurrentHistoryEntity>>

}