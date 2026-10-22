package com.kinemaster.app.database.project;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import kotlin.C2354Metadata;

@Database(entities = {ProjectEntity.class}, exportSchema = false, version = 3)
@C2354Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b'\u0018\u0000 \u00072\u00020\u0001:\u0002\b\tB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H&¢\u0006\u0004\b\u0005\u0010\u0006¨\u0006\n"}, d2 = {"Lcom/kinemaster/app/database/project/ProjectDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "Lcom/kinemaster/app/database/project/a;", "projectDao", "()Lcom/kinemaster/app/database/project/a;", "Companion", "com/kinemaster/app/database/project/c", "c17", "KineMaster"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public abstract class ProjectDatabase extends RoomDatabase {
    public static final c17 Companion = new Object();
    /* access modifiers changed from: private */
    public static volatile ProjectDatabase INSTANCE = null;
    private static final String LOG_TAG = "ProjectDatabase";
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_2_3 = new v73(2, 3, 11);

    public abstract a projectDao();
}
