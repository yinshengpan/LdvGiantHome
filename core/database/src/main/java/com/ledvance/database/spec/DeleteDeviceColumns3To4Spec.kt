package com.ledvance.database.spec

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 12/17/25 17:51
 * Describe : DeleteDeviceColumns3To4Spec
 */
import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn.Entries(
    DeleteColumn(
        tableName = "devices",
        columnName = "paired_charger_list"
    ),
    DeleteColumn(
        tableName = "devices",
        columnName = "available_charger_list"
    )
)
class DeleteDeviceColumns3To4Spec : AutoMigrationSpec