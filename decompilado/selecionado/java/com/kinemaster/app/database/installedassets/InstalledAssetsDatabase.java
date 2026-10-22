package com.kinemaster.app.database.installedassets;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.C2354Metadata;

@Database(entities = {InstalledAssetCategory.class, InstalledAssetSubcategory.class, InstalledAsset.class, InstalledAssetItem.class, InstalledAssetBy.class, InstalledAssetAIModel.class}, exportSchema = false, version = 8)
@C2354Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b'\u0018\u0000 \u00162\u00020\u0001:\u0001\u0017B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H&¢\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\b\u001a\u00020\u0007H&¢\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000b\u001a\u00020\nH&¢\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\u000e\u001a\u00020\rH&¢\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0011\u001a\u00020\u0010H&¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0014\u001a\u00020\u0013H&¢\u0006\u0004\b\u0014\u0010\u0015¨\u0006\u0018"}, d2 = {"Lcom/kinemaster/app/database/installedassets/InstalledAssetsDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "Lq64;", "getInstalledAssetCategoryDao", "()Lq64;", "Le74;", "getInstalledAssetSubcategoryDao", "()Le74;", "Ll64;", "getInstalledAssetByDao", "()Ll64;", "Lt64;", "getInstalledAssetDao", "()Lt64;", "Lz64;", "getInstalledAssetItemDao", "()Lz64;", "Lh64;", "getInstalledAssetAiModelDao", "()Lh64;", "Companion", "i74", "KineMaster"}, k = 1, mv = {2, 3, 0}, xi = 48)
@TypeConverters({t94.class})
/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public abstract class InstalledAssetsDatabase extends RoomDatabase {
    public static final i74 Companion = new Object();
    private static final String DATABASE_NAME = "installed_assets_database";
    /* access modifiers changed from: private */
    public static InstalledAssetsDatabase INSTANCE;
    /* access modifiers changed from: private */
    public static final ExecutorService IO_EXECUTOR = Executors.newSingleThreadExecutor();
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_1_2 = new v73(1, 2, 5);
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_2_3 = new v73(2, 3, 6);
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_3_4 = new v73(3, 4, 7);
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_4_5 = new v73(4, 5, 8);
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_5_6 = new v73(5, 6, 9);
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_6_7 = new v73(6, 7, 10);
    /* access modifiers changed from: private */
    public static final Migration MIGRATION_7_8 = new g74();

    public abstract h64 getInstalledAssetAiModelDao();

    public abstract l64 getInstalledAssetByDao();

    public abstract q64 getInstalledAssetCategoryDao();

    public abstract t64 getInstalledAssetDao();

    public abstract z64 getInstalledAssetItemDao();

    public abstract e74 getInstalledAssetSubcategoryDao();
}
