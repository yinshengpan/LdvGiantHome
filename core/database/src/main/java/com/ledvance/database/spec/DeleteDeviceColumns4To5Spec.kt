package com.ledvance.database.spec

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 12/22/25 11:51
 * Describe : DeleteDeviceColumns4To5Spec
 */
@DeleteColumn.Entries(
    DeleteColumn(
        tableName = "devices",
        columnName = "l1"
    ),
    DeleteColumn(
        tableName = "devices",
        columnName = "l2"
    ),
    DeleteColumn(
        tableName = "devices",
        columnName = "trip_current"
    ),
    DeleteColumn(
        tableName = "devices",
        columnName = "charge_current"
    ),
    DeleteColumn(
        tableName = "chargers",
        columnName = "charge_current"
    )
)
class DeleteDeviceColumns4To5Spec : AutoMigrationSpec