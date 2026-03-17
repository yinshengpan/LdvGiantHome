package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ledvance.database.model.ChargerEntity
import kotlinx.coroutines.flow.Flow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/18/25 11:46
 * Describe : ChargerDao
 */
@Dao
interface ChargerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: ChargerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(devices: List<ChargerEntity>)

    @Query("select * from chargers where box_address=:address and is_paired=:isPaired")
    suspend fun getChargerList(address: String, isPaired: Boolean): List<ChargerEntity>

    @Query("select * from chargers where box_address=:address")
    fun getAllChargerListFlow(address: String): Flow<List<ChargerEntity>>

    @Query("select * from chargers where box_address=:address")
    suspend fun getAllChargerList(address: String): List<ChargerEntity>

    @Query("update chargers set is_paired=:isPaired where charger_number=:chargerNumber")
    suspend fun updateChargerStatus(chargerNumber: String, isPaired: Boolean)

    @Query("delete from chargers where box_address=:address and is_paired=:isPaired and charger_number not in (:keepChargerList)")
    suspend fun deleteNotIn(address: String, isPaired: Boolean, keepChargerList: List<String>)

    @Update
    suspend fun update(devices: List<ChargerEntity>)
}