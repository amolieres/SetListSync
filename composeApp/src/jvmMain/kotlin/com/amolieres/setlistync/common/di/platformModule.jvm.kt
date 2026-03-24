package com.amolieres.setlistync.common.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.amolieres.setlistync.common.util.createDataStore
import com.amolieres.setlistync.common.util.getRoomDatabase
import com.amolieres.setlistync.core.data.local.SetListSyncDatabase
import com.amolieres.setlistync.common.util.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<SetListSyncDatabase> {
            getRoomDatabase(getDatabaseBuilder())
        }
        single<DataStore<Preferences>> {
            createDataStore()
        }
    }
