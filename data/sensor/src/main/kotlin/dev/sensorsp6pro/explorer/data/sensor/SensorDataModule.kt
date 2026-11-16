package dev.sensorsp6pro.explorer.data.sensor

import android.content.Context
import android.hardware.SensorManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sensorsp6pro.explorer.core.domain.AccelerationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorDataModule {
    @Binds
    @Singleton
    abstract fun bindAccelerationRepository(
        implementation: SensorManagerAccelerationRepository,
    ): AccelerationRepository

    companion object {
        @Provides
        @Singleton
        fun provideSensorManager(@ApplicationContext context: Context): SensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}
