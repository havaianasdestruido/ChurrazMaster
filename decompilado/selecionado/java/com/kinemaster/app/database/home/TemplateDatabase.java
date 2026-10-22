package com.kinemaster.app.database.home;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import kotlin.C2354Metadata;

@Database(entities = {TemplateEntity.class, LikeEntity.class, BlockUserEntity.class, UserTemplateEntity.class, RemoteKeyEntity.class, KeywordEntity.class, HashtagKeywordEntity.class, FollowEntity.class, UserEntity.class, InboxEntity.class, TemplatePinEntity.class, TemplateSectionEntity.class, CommentEntity.class, ReplyCommentEntity.class, CommentTotalCountEntity.class}, exportSchema = false, version = 9, views = {c99.class})
@C2354Metadata(d1 = {"\u0000\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b'\u0018\u0000 12\u00020\u0001:\u00012B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H&¢\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\b\u001a\u00020\u0007H&¢\u0006\u0004\b\b\u0010\tJ\u000f\u0010\u000b\u001a\u00020\nH&¢\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\u000e\u001a\u00020\rH&¢\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0011\u001a\u00020\u0010H&¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0014\u001a\u00020\u0013H&¢\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0017\u001a\u00020\u0016H&¢\u0006\u0004\b\u0017\u0010\u0018J\u000f\u0010\u001a\u001a\u00020\u0019H&¢\u0006\u0004\b\u001a\u0010\u001bJ\u000f\u0010\u001d\u001a\u00020\u001cH&¢\u0006\u0004\b\u001d\u0010\u001eJ\u000f\u0010 \u001a\u00020\u001fH&¢\u0006\u0004\b \u0010!J\u000f\u0010#\u001a\u00020\"H&¢\u0006\u0004\b#\u0010$J\u000f\u0010&\u001a\u00020%H&¢\u0006\u0004\b&\u0010'J\u000f\u0010)\u001a\u00020(H&¢\u0006\u0004\b)\u0010*J\u000f\u0010,\u001a\u00020+H&¢\u0006\u0004\b,\u0010-J\u000f\u0010/\u001a\u00020.H&¢\u0006\u0004\b/\u00100¨\u00063"}, d2 = {"Lcom/kinemaster/app/database/home/TemplateDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "La05;", "likesDao", "()La05;", "Lcom/kinemaster/app/database/home/b;", "blockUserDao", "()Lcom/kinemaster/app/database/home/b;", "Ln0a;", "userTemplateDao", "()Ln0a;", "Ly89;", "templatePinDao", "()Ly89;", "Lc59;", "templateDao", "()Lc59;", "Luk7;", "remoteKeyDao", "()Luk7;", "La99;", "templateSectionDao", "()La99;", "Ltn4;", "keywordDao", "()Ltn4;", "Lno3;", "hashtagKeywordDao", "()Lno3;", "Lc43;", "followDao", "()Lc43;", "Lj0a;", "userDao", "()Lj0a;", "Lx24;", "inboxDao", "()Lx24;", "Led1;", "templateCommentDao", "()Led1;", "Lul7;", "templateReplyCommentDao", "()Lul7;", "Lmf1;", "templateCommentTotalCountDao", "()Lmf1;", "Companion", "i59", "KineMaster"}, k = 1, mv = {2, 3, 0}, xi = 48)
@TypeConverters({cr8.class})
/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public abstract class TemplateDatabase extends RoomDatabase {
    public static final i59 Companion = new Object();

    public abstract b blockUserDao();

    public abstract c43 followDao();

    public abstract no3 hashtagKeywordDao();

    public abstract x24 inboxDao();

    public abstract tn4 keywordDao();

    public abstract a05 likesDao();

    public abstract uk7 remoteKeyDao();

    public abstract ed1 templateCommentDao();

    public abstract mf1 templateCommentTotalCountDao();

    public abstract c59 templateDao();

    public abstract y89 templatePinDao();

    public abstract ul7 templateReplyCommentDao();

    public abstract a99 templateSectionDao();

    public abstract j0a userDao();

    public abstract n0a userTemplateDao();
}
