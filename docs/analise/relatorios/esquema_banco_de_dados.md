# Esquema dos bancos Room (reconstruído estaticamente)

Gerado em `2026-10-22T16:06:02.294980+00:00` por `scripts/11_esquema_room.py`.

- **Bancos (`@Database`)**: 12
- **Entidades (`@Entity`)**: 49
- **Campos declarados**: 517
- **Consultas `@Query` encontradas**: 0

> Limitação: nomes de tabela/coluna vêm das anotações; quando `tableName` ou
> `@ColumnInfo` estão ausentes, o valor é o nome da classe/campo (regra do Room) —
> sinalizado como inferido. Nenhum banco foi aberto em execução.

## `AudioSearchKeywordDatabase` (versão 2)

Arquivo: `com/kinemaster/app/screen/projecteditor/browser/audio/data/AudioSearchKeywordDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `audio_search_keyword` | `AudioSearchKeyword` | `keyword` | 2 |

<details><summary><code>audio_search_keyword</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `date` | `Date` | `(mesmo nome)` |
| `keyword` | `String` | `keyword` |

</details>

## `FontDatabase` (versão 6)

Arquivo: `com/kinemaster/app/database/font/FontDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `font_table` | `FontEntity` | `id` | 15 |
| `font_collection_table` | `FontCollectionEntity` | `collectionId` | 9 |
| `font_collection_order_table` | `FontCollectionOrderEntity` | `collectionId` | 2 |
| `font_collection_remote_keys` | `FontCollectionRemoteKeyEntity` | `collectionId` | 3 |
| `FontOrderEntity *(inferida)*` | `FontOrderEntity` | `—` | 4 |
| `FontStateEntity *(inferida)*` | `FontStateEntity` | `itemId` | 8 |
| `FontSearchEntity *(inferida)*` | `FontSearchEntity` | `—` | 4 |

<details><summary><code>font_table</code> — 15 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `BUILTIN_FONT_ID_PREFIX` | `static final String` | `(mesmo nome)` |
| `Companion` | `static final z73` | `(mesmo nome)` |
| `IMPORTED_FONT_ID_PREFIX` | `static final String` | `(mesmo nome)` |
| `SAMPLE_IMAGE_HEIGHT` | `static final int` | `(mesmo nome)` |
| `SAMPLE_IMAGE_WIDTH` | `static final int` | `(mesmo nome)` |
| `SYSTEM_FONT_ID_PREFIX` | `static final String` | `(mesmo nome)` |
| `TABLE_NAME` | `static final String` | `(mesmo nome)` |
| `assetId` | `String` | `assetIdx` |
| `assetIdx` | `int` | `assetIdx` |
| `cacheTypeface` | `WeakReference<Typeface>` | `(mesmo nome)` |
| `collectionId` | `String` | `collectionId` |
| `id` | `String` | `id` |
| `name` | `String` | `thumbnail` |
| `path` | `String` | `path` |
| `thumbnail` | `String` | `thumbnail` |

</details>

<details><summary><code>font_collection_table</code> — 9 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final l73` | `(mesmo nome)` |
| `FAVORITE_FONT_COLLECTION_ID` | `static final String` | `(mesmo nome)` |
| `INTERNAL_COLLECTIONS` | `static final List<String>` | `(mesmo nome)` |
| `LATIN_COLLECTION_ID` | `static final String` | `(mesmo nome)` |
| `MY_FONT_COLLECTION_ID` | `static final String` | `(mesmo nome)` |
| `SYSTEM_COLLECTION_ID` | `static final String` | `(mesmo nome)` |
| `collectionId` | `String` | `(mesmo nome)` |
| `language` | `String` | `(mesmo nome)` |
| `name` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>font_collection_order_table</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `collectionId` | `String` | `(mesmo nome)` |
| `orderIndex` | `int` | `(mesmo nome)` |

</details>

<details><summary><code>font_collection_remote_keys</code> — 3 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `collectionId` | `String` | `(mesmo nome)` |
| `nextKeyJson` | `y20` | `(mesmo nome)` |
| `prevKeyJson` | `y20` | `(mesmo nome)` |

</details>

<details><summary><code>FontOrderEntity</code> — 4 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `collectionId` | `String` | `(mesmo nome)` |
| `itemId` | `String` | `(mesmo nome)` |
| `orderIndex` | `int` | `(mesmo nome)` |
| `version` | `long` | `(mesmo nome)` |

</details>

<details><summary><code>FontStateEntity</code> — 8 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `collectionId` | `String` | `(mesmo nome)` |
| `createdTime` | `long` | `(mesmo nome)` |
| `favorite` | `int` | `(mesmo nome)` |
| `installed` | `int` | `(mesmo nome)` |
| `itemId` | `String` | `(mesmo nome)` |
| `lastModified` | `long` | `(mesmo nome)` |
| `lastUsedTime` | `long` | `(mesmo nome)` |
| `selected` | `int` | `(mesmo nome)` |

</details>

<details><summary><code>FontSearchEntity</code> — 4 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `itemId` | `String` | `(mesmo nome)` |
| `keyword` | `String` | `(mesmo nome)` |
| `orderIndex` | `int` | `(mesmo nome)` |
| `version` | `long` | `(mesmo nome)` |

</details>

## `HotFeaturedDatabase` (versão 4)

Arquivo: `com/kinemaster/module/network/remote/service/store/data/database/HotFeaturedDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `AssetEntity` | `AssetEntity` | `assetIdx` | 41 |

<details><summary><code>AssetEntity</code> — 41 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final gx` | `(mesmo nome)` |
| `POST_FIX_THUMBNAIL_LARGE` | `static final String` | `(mesmo nome)` |
| `POST_FIX_THUMBNAIL_SMALL` | `static final String` | `(mesmo nome)` |
| `assetId` | `String` | `(mesmo nome)` |
| `assetIdx` | `int` | `(mesmo nome)` |
| `assetName` | `List<Localization>` | `(mesmo nome)` |
| `assetServerVersion` | `int` | `(mesmo nome)` |
| `assetSize` | `long` | `(mesmo nome)` |
| `assetType` | `int` | `(mesmo nome)` |
| `assetUrl` | `String` | `(mesmo nome)` |
| `assetVersion` | `int` | `(mesmo nome)` |
| `audioPath` | `String` | `(mesmo nome)` |
| `availablePurchase` | `int` | `(mesmo nome)` |
| `cacheVersion` | `int` | `(mesmo nome)` |
| `cachedTime` | `long` | `(mesmo nome)` |
| `categoryAliasName` | `String` | `(mesmo nome)` |
| `categoryIdx` | `int` | `(mesmo nome)` |
| `categoryImageUrl` | `String` | `(mesmo nome)` |
| `categoryImageUrlOn` | `String` | `(mesmo nome)` |
| `categoryName` | `List<Localization>` | `(mesmo nome)` |
| `creator` | `String` | `(mesmo nome)` |
| `defaultFlag` | `int` | `(mesmo nome)` |
| `description` | `String` | `(mesmo nome)` |
| `duration` | `int` | `(mesmo nome)` |
| `expireTime` | `long` | `(mesmo nome)` |
| `featuredOrder` | `int` | `(mesmo nome)` |
| `fromCategory` | `boolean` | `(mesmo nome)` |
| `hasDetail` | `boolean` | `(mesmo nome)` |
| `languageIdx` | `int` | `(mesmo nome)` |
| `payFee` | `String` | `(mesmo nome)` |
| `priceType` | `String` | `(mesmo nome)` |
| `productId` | `String` | `(mesmo nome)` |
| `publishTime` | `long` | `(mesmo nome)` |
| `subcategoryAliasName` | `String` | `(mesmo nome)` |
| `subcategoryIdx` | `int` | `(mesmo nome)` |
| `subcategoryName` | `List<Localization>` | `(mesmo nome)` |
| `thumbnailUrl` | `String` | `(mesmo nome)` |
| `thumbnails` | `List<Thumbnail>` | `(mesmo nome)` |
| `title` | `String` | `(mesmo nome)` |
| `updateTime` | `long` | `(mesmo nome)` |
| `videoPath` | `String` | `(mesmo nome)` |

</details>

## `InstalledAssetsDatabase` (versão 8)

Arquivo: `com/kinemaster/app/database/installedassets/InstalledAssetsDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `installed_asset_category` | `InstalledAssetCategory` | `categoryId` | 6 |
| `installed_asset_subcategory` | `InstalledAssetSubcategory` | `_id` | 7 |
| `installed_asset` | `InstalledAsset` | `assetId` | 22 |
| `installed_asset_item` | `InstalledAssetItem` | `itemId` | 27 |
| `installed_asset_by` | `InstalledAssetBy` | `id` | 5 |
| `installed_asset_ai_models` | `InstalledAssetAIModel` | `itemId` | 18 |

<details><summary><code>installed_asset_category</code> — 6 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final p64` | `(mesmo nome)` |
| `TABLE_NAME` | `static final String` | `(mesmo nome)` |
| `categoryAliasName` | `String` | `(mesmo nome)` |
| `categoryIconURL` | `String` | `(mesmo nome)` |
| `categoryId` | `String` | `(mesmo nome)` |
| `categoryName` | `Map<String, String>` | `(mesmo nome)` |

</details>

<details><summary><code>installed_asset_subcategory</code> — 7 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final d74` | `(mesmo nome)` |
| `TABLE_NAME` | `static final String` | `(mesmo nome)` |
| `_id` | `String` | `subcategory_id` |
| `categoryId` | `String` | `(mesmo nome)` |
| `subcategoryAliasName` | `String` | `(mesmo nome)` |
| `subcategoryId` | `String` | `(mesmo nome)` |
| `subcategoryName` | `Map<String, String>` | `(mesmo nome)` |

</details>

<details><summary><code>installed_asset</code> — 22 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final f64` | `(mesmo nome)` |
| `POST_FIX_THUMBNAIL_SMALL` | `static final String` | `(mesmo nome)` |
| `TABLE_NAME` | `static final String` | `(mesmo nome)` |
| `assetDesc` | `String` | `(mesmo nome)` |
| `assetId` | `String` | `(mesmo nome)` |
| `assetIdx` | `int` | `(mesmo nome)` |
| `assetLevel` | `String` | `(mesmo nome)` |
| `assetName` | `Map<String, String>` | `(mesmo nome)` |
| `assetSize` | `int` | `(mesmo nome)` |
| `assetUrl` | `String` | `(mesmo nome)` |
| `assetVersion` | `int` | `(mesmo nome)` |
| `categoryId` | `String` | `(mesmo nome)` |
| `hasUpdate` | `int` | `(mesmo nome)` |
| `installedById` | `String` | `(mesmo nome)` |
| `localPath` | `String` | `(mesmo nome)` |
| `packageURI` | `String` | `(mesmo nome)` |
| `priceType` | `String` | `(mesmo nome)` |
| `reviewStatus` | `String` | `(mesmo nome)` |
| `subcategoryId` | `String` | `(mesmo nome)` |
| `thumbPath` | `String` | `(mesmo nome)` |
| `thumbUrl` | `String` | `(mesmo nome)` |
| `updatedTime` | `long` | `(mesmo nome)` |

</details>

<details><summary><code>installed_asset_item</code> — 27 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final x64` | `(mesmo nome)` |
| `TABLE_NAME` | `static final String` | `(mesmo nome)` |
| `assetId` | `String` | `(mesmo nome)` |
| `assetIdx` | `int` | `(mesmo nome)` |
| `categoryId` | `String` | `(mesmo nome)` |
| `createdTime` | `long` | `(mesmo nome)` |
| `duration` | `String` | `duration` |
| `favorite` | `int` | `favorite` |
| `filePath` | `String` | `(mesmo nome)` |
| `filePathRoot` | `String` | `(mesmo nome)` |
| `hidden` | `int` | `hidden` |
| `iconPath` | `String` | `(mesmo nome)` |
| `itemCategory` | `String` | `(mesmo nome)` |
| `itemId` | `String` | `(mesmo nome)` |
| `itemSubType` | `String` | `(mesmo nome)` |
| `itemType` | `String` | `(mesmo nome)` |
| `kmCategory` | `List<String>` | `(mesmo nome)` |
| `label` | `Map<String, String>` | `label` |
| `legacyId` | `String` | `(mesmo nome)` |
| `packageURI` | `String` | `(mesmo nome)` |
| `priceType` | `String` | `(mesmo nome)` |
| `ratios` | `List<String>` | `ratios` |
| `sampleMediaFile` | `String` | `(mesmo nome)` |
| `sampleText` | `String` | `(mesmo nome)` |
| `subcategoryId` | `String` | `(mesmo nome)` |
| `thumbPath` | `String` | `(mesmo nome)` |
| `updatedTime` | `long` | `(mesmo nome)` |

</details>

<details><summary><code>installed_asset_by</code> — 5 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final k64` | `(mesmo nome)` |
| `TABLE_NAME` | `static final String` | `(mesmo nome)` |
| `id` | `String` | `id` |
| `latestVersion` | `long` | `(mesmo nome)` |
| `type` | `InstalledAssetByType` | `type` |

</details>

<details><summary><code>installed_asset_ai_models</code> — 18 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final g64` | `(mesmo nome)` |
| `TABLE_NAME` | `static final String` | `(mesmo nome)` |
| `aiType` | `String` | `(mesmo nome)` |
| `combinedTFLite` | `String` | `(mesmo nome)` |
| `decodingTFLite` | `String` | `(mesmo nome)` |
| `encodingTFLite` | `String` | `(mesmo nome)` |
| `gender` | `String` | `gender` |
| `itemId` | `String` | `(mesmo nome)` |
| `language` | `String` | `language_id` |
| `languageId` | `int` | `(mesmo nome)` |
| `languageIdInternal` | `int` | `(mesmo nome)` |
| `modelType` | `int` | `(mesmo nome)` |
| `outputSamplerate` | `int` | `(mesmo nome)` |
| `scales` | `float[]` | `scales` |
| `speakerId` | `int` | `(mesmo nome)` |
| `targetFormant` | `int` | `(mesmo nome)` |
| `tokenizerType` | `int` | `(mesmo nome)` |
| `updateTime` | `long` | `(mesmo nome)` |

</details>

## `LimitDatabase` (versão 3)

Arquivo: `com/kinemaster/app/database/export/LimitDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `export_count` | `ExportCountEntity` | `resolution` | 4 |
| `menu_action_count` | `MenuActionCountEntity` | `action` | 3 |

<details><summary><code>export_count</code> — 4 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `count` | `int` | `(mesmo nome)` |
| `countingStartTimeMS` | `long` | `(mesmo nome)` |
| `resolution` | `int` | `(mesmo nome)` |
| `updateTimeMS` | `long` | `(mesmo nome)` |

</details>

<details><summary><code>menu_action_count</code> — 3 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `action` | `String` | `(mesmo nome)` |
| `count` | `int` | `(mesmo nome)` |
| `updateTimeMS` | `long` | `(mesmo nome)` |

</details>

## `NewFeaturedDatabase` (versão 4)

Arquivo: `com/kinemaster/module/network/remote/service/store/data/database/NewFeaturedDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `AssetEntity` | `AssetEntity` | `assetIdx` | 41 |

<details><summary><code>AssetEntity</code> — 41 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final gx` | `(mesmo nome)` |
| `POST_FIX_THUMBNAIL_LARGE` | `static final String` | `(mesmo nome)` |
| `POST_FIX_THUMBNAIL_SMALL` | `static final String` | `(mesmo nome)` |
| `assetId` | `String` | `(mesmo nome)` |
| `assetIdx` | `int` | `(mesmo nome)` |
| `assetName` | `List<Localization>` | `(mesmo nome)` |
| `assetServerVersion` | `int` | `(mesmo nome)` |
| `assetSize` | `long` | `(mesmo nome)` |
| `assetType` | `int` | `(mesmo nome)` |
| `assetUrl` | `String` | `(mesmo nome)` |
| `assetVersion` | `int` | `(mesmo nome)` |
| `audioPath` | `String` | `(mesmo nome)` |
| `availablePurchase` | `int` | `(mesmo nome)` |
| `cacheVersion` | `int` | `(mesmo nome)` |
| `cachedTime` | `long` | `(mesmo nome)` |
| `categoryAliasName` | `String` | `(mesmo nome)` |
| `categoryIdx` | `int` | `(mesmo nome)` |
| `categoryImageUrl` | `String` | `(mesmo nome)` |
| `categoryImageUrlOn` | `String` | `(mesmo nome)` |
| `categoryName` | `List<Localization>` | `(mesmo nome)` |
| `creator` | `String` | `(mesmo nome)` |
| `defaultFlag` | `int` | `(mesmo nome)` |
| `description` | `String` | `(mesmo nome)` |
| `duration` | `int` | `(mesmo nome)` |
| `expireTime` | `long` | `(mesmo nome)` |
| `featuredOrder` | `int` | `(mesmo nome)` |
| `fromCategory` | `boolean` | `(mesmo nome)` |
| `hasDetail` | `boolean` | `(mesmo nome)` |
| `languageIdx` | `int` | `(mesmo nome)` |
| `payFee` | `String` | `(mesmo nome)` |
| `priceType` | `String` | `(mesmo nome)` |
| `productId` | `String` | `(mesmo nome)` |
| `publishTime` | `long` | `(mesmo nome)` |
| `subcategoryAliasName` | `String` | `(mesmo nome)` |
| `subcategoryIdx` | `int` | `(mesmo nome)` |
| `subcategoryName` | `List<Localization>` | `(mesmo nome)` |
| `thumbnailUrl` | `String` | `(mesmo nome)` |
| `thumbnails` | `List<Thumbnail>` | `(mesmo nome)` |
| `title` | `String` | `(mesmo nome)` |
| `updateTime` | `long` | `(mesmo nome)` |
| `videoPath` | `String` | `(mesmo nome)` |

</details>

## `ProjectDatabase` (versão 3)

Arquivo: `com/kinemaster/app/database/project/ProjectDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `project_table` | `ProjectEntity` | `uuid` | 11 |

<details><summary><code>project_table</code> — 11 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `createFileTime` | `long` | `(mesmo nome)` |
| `createTime` | `long` | `(mesmo nome)` |
| `duration` | `long` | `(mesmo nome)` |
| `file` | `String` | `(mesmo nome)` |
| `lastEditTime` | `long` | `(mesmo nome)` |
| `lastFileTime` | `long` | `(mesmo nome)` |
| `ratio` | `float` | `(mesmo nome)` |
| `size` | `long` | `(mesmo nome)` |
| `thumbnail` | `String` | `(mesmo nome)` |
| `title` | `String` | `(mesmo nome)` |
| `uuid` | `String` | `(mesmo nome)` |

</details>

## `SaveAsVideoDatabase` (versão 3)

Arquivo: `com/kinemaster/app/database/saveas/SaveAsVideoDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `videos` | `SaveAsVideo` | `id` | 10 |

<details><summary><code>videos</code> — 10 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `creationTime` | `long` | `(mesmo nome)` |
| `height` | `int` | `(mesmo nome)` |
| `id` | `int` | `(mesmo nome)` |
| `lastEditTime` | `long` | `(mesmo nome)` |
| `path` | `String` | `(mesmo nome)` |
| `projectName` | `String` | `(mesmo nome)` |
| `projectUUID` | `String` | `(mesmo nome)` |
| `projectVersion` | `int` | `(mesmo nome)` |
| `useMuserkAudio` | `boolean` | `(mesmo nome)` |
| `width` | `int` | `(mesmo nome)` |

</details>

## `SearchRecentDatabase` (versão 1)

Arquivo: `com/kinemaster/app/screen/home/template/search/database/SearchRecentDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `search_recent` | `SearchRecentEntity` | `title` | 2 |

<details><summary><code>search_recent</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `timestamp` | `long` | `timestamp` |
| `title` | `String` | `title` |

</details>

## `StoreDatabase` (versão 8)

Arquivo: `com/kinemaster/module/network/remote/service/store/data/database/StoreDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `AssetEntity` | `AssetEntity` | `assetIdx` | 41 |
| `CategoryEntity` | `CategoryEntity` | `categoryIdx` | 8 |
| `SearchTable` | `SearchEntity` | `id` | 6 |
| `CacheEntity` | `CacheEntity` | `id` | 8 |
| `FeatureEntity` | `FeatureEntity` | `id` | 4 |
| `EditorPickAssetEntity` | `EditorPickAssetEntity` | `id` | 21 |

<details><summary><code>AssetEntity</code> — 41 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final gx` | `(mesmo nome)` |
| `POST_FIX_THUMBNAIL_LARGE` | `static final String` | `(mesmo nome)` |
| `POST_FIX_THUMBNAIL_SMALL` | `static final String` | `(mesmo nome)` |
| `assetId` | `String` | `(mesmo nome)` |
| `assetIdx` | `int` | `(mesmo nome)` |
| `assetName` | `List<Localization>` | `(mesmo nome)` |
| `assetServerVersion` | `int` | `(mesmo nome)` |
| `assetSize` | `long` | `(mesmo nome)` |
| `assetType` | `int` | `(mesmo nome)` |
| `assetUrl` | `String` | `(mesmo nome)` |
| `assetVersion` | `int` | `(mesmo nome)` |
| `audioPath` | `String` | `(mesmo nome)` |
| `availablePurchase` | `int` | `(mesmo nome)` |
| `cacheVersion` | `int` | `(mesmo nome)` |
| `cachedTime` | `long` | `(mesmo nome)` |
| `categoryAliasName` | `String` | `(mesmo nome)` |
| `categoryIdx` | `int` | `(mesmo nome)` |
| `categoryImageUrl` | `String` | `(mesmo nome)` |
| `categoryImageUrlOn` | `String` | `(mesmo nome)` |
| `categoryName` | `List<Localization>` | `(mesmo nome)` |
| `creator` | `String` | `(mesmo nome)` |
| `defaultFlag` | `int` | `(mesmo nome)` |
| `description` | `String` | `(mesmo nome)` |
| `duration` | `int` | `(mesmo nome)` |
| `expireTime` | `long` | `(mesmo nome)` |
| `featuredOrder` | `int` | `(mesmo nome)` |
| `fromCategory` | `boolean` | `(mesmo nome)` |
| `hasDetail` | `boolean` | `(mesmo nome)` |
| `languageIdx` | `int` | `(mesmo nome)` |
| `payFee` | `String` | `(mesmo nome)` |
| `priceType` | `String` | `(mesmo nome)` |
| `productId` | `String` | `(mesmo nome)` |
| `publishTime` | `long` | `(mesmo nome)` |
| `subcategoryAliasName` | `String` | `(mesmo nome)` |
| `subcategoryIdx` | `int` | `(mesmo nome)` |
| `subcategoryName` | `List<Localization>` | `(mesmo nome)` |
| `thumbnailUrl` | `String` | `(mesmo nome)` |
| `thumbnails` | `List<Thumbnail>` | `(mesmo nome)` |
| `title` | `String` | `(mesmo nome)` |
| `updateTime` | `long` | `(mesmo nome)` |
| `videoPath` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>CategoryEntity</code> — 8 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `categoryAliasName` | `String` | `(mesmo nome)` |
| `categoryIdx` | `int` | `(mesmo nome)` |
| `categoryName` | `List<Localization>` | `(mesmo nome)` |
| `imageUrl` | `String` | `(mesmo nome)` |
| `imageUrlOn` | `String` | `(mesmo nome)` |
| `order` | `int` | `(mesmo nome)` |
| `subCategory` | `List<es8>` | `(mesmo nome)` |
| `viewType` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>SearchTable</code> — 6 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `assetIdx` | `int` | `(mesmo nome)` |
| `categoryIdx` | `int` | `(mesmo nome)` |
| `description` | `String` | `(mesmo nome)` |
| `id` | `String` | `(mesmo nome)` |
| `language` | `String` | `(mesmo nome)` |
| `title` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>CacheEntity</code> — 8 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `assetLevel` | `int` | `(mesmo nome)` |
| `cachedTime` | `long` | `(mesmo nome)` |
| `cachedVersion` | `int` | `(mesmo nome)` |
| `categoryIdx` | `int` | `(mesmo nome)` |
| `editorPickIdx` | `int` | `(mesmo nome)` |
| `id` | `long` | `(mesmo nome)` |
| `subCategory` | `int` | `(mesmo nome)` |
| `type` | `int` | `(mesmo nome)` |

</details>

<details><summary><code>FeatureEntity</code> — 4 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `assetIdx` | `int` | `(mesmo nome)` |
| `assetOrder` | `int` | `(mesmo nome)` |
| `featureIndex` | `int` | `(mesmo nome)` |
| `id` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>EditorPickAssetEntity</code> — 21 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `assetId` | `String` | `(mesmo nome)` |
| `assetIdx` | `int` | `(mesmo nome)` |
| `assetName` | `List<Localization>` | `(mesmo nome)` |
| `assetServerVersion` | `int` | `(mesmo nome)` |
| `assetSize` | `long` | `(mesmo nome)` |
| `assetType` | `int` | `(mesmo nome)` |
| `assetUrl` | `String` | `(mesmo nome)` |
| `assetVersion` | `int` | `(mesmo nome)` |
| `audioPath` | `String` | `(mesmo nome)` |
| `description` | `String` | `(mesmo nome)` |
| `duration` | `int` | `(mesmo nome)` |
| `editorPickIdx` | `int` | `(mesmo nome)` |
| `id` | `int` | `id` |
| `itemCategory` | `String` | `(mesmo nome)` |
| `payFee` | `String` | `(mesmo nome)` |
| `priceType` | `String` | `(mesmo nome)` |
| `publishTime` | `long` | `(mesmo nome)` |
| `thumbnailUrl` | `String` | `(mesmo nome)` |
| `title` | `String` | `(mesmo nome)` |
| `updateTime` | `long` | `(mesmo nome)` |
| `videoPath` | `String` | `(mesmo nome)` |

</details>

## `TemplateDatabase` (versão 9)

Arquivo: `com/kinemaster/app/database/home/TemplateDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `template_entity` | `TemplateEntity` | `projectId` | 49 |
| `likes_entity` | `LikeEntity` | `id` | 5 |
| `blockuser_entity` | `BlockUserEntity` | `userId` | 6 |
| `user_template_entity` | `UserTemplateEntity` | `id` | 4 |
| `remote_key_entity` | `RemoteKeyEntity` | `keyId` | 3 |
| `keyword_entity` | `KeywordEntity` | `id` | 3 |
| `hashtag_keyword_entity` | `HashtagKeywordEntity` | `hashtag` | 2 |
| `follow_entity` | `FollowEntity` | `id` | 10 |
| `user_entity` | `UserEntity` | `id` | 8 |
| `inbox_entity` | `InboxEntity` | `groupId` | 15 |
| `template_pin_entity` | `TemplatePinEntity` | `projectId` | 2 |
| `template_section_entity` | `TemplateSectionEntity` | `sectionId` | 2 |
| `CommentEntity *(inferida)*` | `CommentEntity` | `—` | 14 |
| `ReplyCommentEntity *(inferida)*` | `ReplyCommentEntity` | `—` | 17 |
| `CommentTotalCountEntity *(inferida)*` | `CommentTotalCountEntity` | `—` | 2 |

<details><summary><code>template_entity</code> — 49 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `appName` | `String` | `(mesmo nome)` |
| `assetLevel` | `int` | `(mesmo nome)` |
| `author` | `String` | `(mesmo nome)` |
| `authorIsOfficial` | `boolean` | `(mesmo nome)` |
| `authorNickName` | `String` | `(mesmo nome)` |
| `authorProfileImage` | `String` | `(mesmo nome)` |
| `authorProfileImageBackground` | `String` | `(mesmo nome)` |
| `authorProfileImageIcon` | `String` | `(mesmo nome)` |
| `authorUserName` | `String` | `(mesmo nome)` |
| `badges` | `List<ProfileBadge>` | `(mesmo nome)` |
| `categoryId` | `String` | `(mesmo nome)` |
| `clips` | `Integer` | `(mesmo nome)` |
| `commentCounts` | `long` | `(mesmo nome)` |
| `commentsDisabled` | `boolean` | `(mesmo nome)` |
| `createdAt` | `String` | `(mesmo nome)` |
| `description` | `String` | `(mesmo nome)` |
| `downloadCounts` | `long` | `(mesmo nome)` |
| `duration` | `String` | `(mesmo nome)` |
| `filePath` | `String` | `(mesmo nome)` |
| `firstFrameImgPath` | `String` | `(mesmo nome)` |
| `hashTags` | `List<String>` | `(mesmo nome)` |
| `height` | `long` | `(mesmo nome)` |
| `imagePath` | `String` | `(mesmo nome)` |
| `index` | `int` | `(mesmo nome)` |
| `isAd` | `boolean` | `(mesmo nome)` |
| `isLiked` | `Boolean` | `(mesmo nome)` |
| `isShared` | `boolean` | `(mesmo nome)` |
| `language` | `String` | `(mesmo nome)` |
| `likeCounts` | `long` | `(mesmo nome)` |
| `likedAt` | `String` | `(mesmo nome)` |
| `mixEditor` | `int` | `(mesmo nome)` |
| `originalTemplate` | `boolean` | `(mesmo nome)` |
| `parentId` | `String` | `(mesmo nome)` |
| `parentImagePath` | `String` | `(mesmo nome)` |
| `pinned` | `boolean` | `(mesmo nome)` |
| `projectId` | `String` | `(mesmo nome)` |
| `projectLevel` | `int` | `(mesmo nome)` |
| `publishedAt` | `String` | `(mesmo nome)` |
| `ratio` | `String` | `(mesmo nome)` |
| `rejectedCategories` | `List<RejectCategory>` | `(mesmo nome)` |
| `rejectedFails` | `List<String>` | `(mesmo nome)` |
| `rejectedReason` | `String` | `(mesmo nome)` |
| `reviewStatus` | `ReviewStatus` | `(mesmo nome)` |
| `shareCounts` | `int` | `(mesmo nome)` |
| `shareDynamicLink` | `String` | `(mesmo nome)` |
| `templateType` | `TemplateType` | `(mesmo nome)` |
| `videoPath` | `String` | `(mesmo nome)` |
| `viewCount` | `int` | `(mesmo nome)` |
| `width` | `long` | `(mesmo nome)` |

</details>

<details><summary><code>likes_entity</code> — 5 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `id` | `int` | `(mesmo nome)` |
| `likeCounts` | `long` | `(mesmo nome)` |
| `likedAt` | `String` | `(mesmo nome)` |
| `projectId` | `String` | `(mesmo nome)` |
| `userId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>blockuser_entity</code> — 6 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `avatar` | `String` | `(mesmo nome)` |
| `blocked` | `boolean` | `(mesmo nome)` |
| `blockedAt` | `String` | `(mesmo nome)` |
| `name` | `String` | `(mesmo nome)` |
| `userId` | `String` | `(mesmo nome)` |
| `userName` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>user_template_entity</code> — 4 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `id` | `int` | `(mesmo nome)` |
| `projectId` | `String` | `(mesmo nome)` |
| `templateViewType` | `TemplateViewType` | `(mesmo nome)` |
| `userId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>remote_key_entity</code> — 3 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `keyId` | `String` | `(mesmo nome)` |
| `nextKey` | `String` | `(mesmo nome)` |
| `startAt` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>keyword_entity</code> — 3 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `id` | `int` | `(mesmo nome)` |
| `keyword` | `String` | `(mesmo nome)` |
| `projectId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>hashtag_keyword_entity</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `count` | `long` | `(mesmo nome)` |
| `hashtag` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>follow_entity</code> — 10 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `avatar` | `String` | `(mesmo nome)` |
| `followId` | `int` | `(mesmo nome)` |
| `id` | `int` | `(mesmo nome)` |
| `isFollower` | `Boolean` | `(mesmo nome)` |
| `isFollowing` | `Boolean` | `(mesmo nome)` |
| `name` | `String` | `(mesmo nome)` |
| `requestUserId` | `Integer` | `(mesmo nome)` |
| `type` | `FollowType` | `(mesmo nome)` |
| `userId` | `int` | `(mesmo nome)` |
| `userName` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>user_entity</code> — 8 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `avatar` | `String` | `(mesmo nome)` |
| `creatorId` | `String` | `(mesmo nome)` |
| `id` | `int` | `(mesmo nome)` |
| `isCreator` | `Boolean` | `(mesmo nome)` |
| `isFollowing` | `Boolean` | `(mesmo nome)` |
| `name` | `String` | `(mesmo nome)` |
| `userId` | `int` | `(mesmo nome)` |
| `userName` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>inbox_entity</code> — 15 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `actionUserId` | `String` | `(mesmo nome)` |
| `avatar` | `String` | `(mesmo nome)` |
| `category` | `String` | `(mesmo nome)` |
| `groupId` | `String` | `(mesmo nome)` |
| `imagePath` | `String` | `(mesmo nome)` |
| `inboxId` | `String` | `(mesmo nome)` |
| `isFollowing` | `boolean` | `(mesmo nome)` |
| `isReadMessage` | `boolean` | `(mesmo nome)` |
| `lastCreatedAt` | `long` | `(mesmo nome)` |
| `message` | `String` | `(mesmo nome)` |
| `notificationType` | `String` | `(mesmo nome)` |
| `otherMessageCount` | `int` | `(mesmo nome)` |
| `requestUserId` | `int` | `(mesmo nome)` |
| `targetId` | `String` | `(mesmo nome)` |
| `uri` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>template_pin_entity</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `projectId` | `String` | `(mesmo nome)` |
| `userId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>template_section_entity</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `sectionId` | `String` | `(mesmo nome)` |
| `title` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>CommentEntity</code> — 14 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `authorIsBlocked` | `boolean` | `(mesmo nome)` |
| `authorName` | `String` | `(mesmo nome)` |
| `authorProfileImage` | `String` | `(mesmo nome)` |
| `authorSub` | `String` | `(mesmo nome)` |
| `authorUsername` | `String` | `(mesmo nome)` |
| `comment` | `String` | `(mesmo nome)` |
| `commentId` | `String` | `(mesmo nome)` |
| `createdAt` | `String` | `(mesmo nome)` |
| `isBlind` | `boolean` | `(mesmo nome)` |
| `isPinned` | `boolean` | `(mesmo nome)` |
| `languageCode` | `String` | `(mesmo nome)` |
| `replyCommentsCount` | `int` | `(mesmo nome)` |
| `templateAuthorSub` | `String` | `(mesmo nome)` |
| `templateId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>ReplyCommentEntity</code> — 17 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `authorName` | `String` | `(mesmo nome)` |
| `authorProfileImage` | `String` | `(mesmo nome)` |
| `authorSub` | `String` | `(mesmo nome)` |
| `authorUsername` | `String` | `(mesmo nome)` |
| `comment` | `String` | `(mesmo nome)` |
| `commentId` | `String` | `(mesmo nome)` |
| `createdAt` | `String` | `(mesmo nome)` |
| `groupCommentId` | `String` | `(mesmo nome)` |
| `isBlind` | `boolean` | `(mesmo nome)` |
| `languageCode` | `String` | `(mesmo nome)` |
| `templateAuthorSub` | `String` | `(mesmo nome)` |
| `templateId` | `String` | `(mesmo nome)` |
| `toCommentId` | `String` | `(mesmo nome)` |
| `toReplyAuthorName` | `String` | `(mesmo nome)` |
| `toReplyAuthorProfileImage` | `String` | `(mesmo nome)` |
| `toReplyAuthorSub` | `String` | `(mesmo nome)` |
| `toReplyAuthorUsername` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>CommentTotalCountEntity</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `templateId` | `String` | `(mesmo nome)` |
| `totalCount` | `int` | `(mesmo nome)` |

</details>

## `WorkDatabase` (versão 24)

Arquivo: `androidx/work/impl/WorkDatabase.java`

| tabela | classe | chave primária | campos |
|---|---|---|---:|
| `Dependency *(inferida)*` | `Dependency` | `—` | 3 |
| `WorkSpec *(inferida)*` | `WorkSpec` | `id` | 29 |
| `WorkTag *(inferida)*` | `WorkTag` | `—` | 2 |
| `SystemIdInfo *(inferida)*` | `SystemIdInfo` | `—` | 3 |
| `WorkName *(inferida)*` | `WorkName` | `—` | 2 |
| `WorkProgress *(inferida)*` | `WorkProgress` | `workSpecId` | 2 |
| `Preference *(inferida)*` | `Preference` | `key` | 2 |

<details><summary><code>Dependency</code> — 3 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `a` | `Qualified` | `(mesmo nome)` |
| `b` | `int` | `(mesmo nome)` |
| `c` | `int` | `(mesmo nome)` |

</details>

<details><summary><code>WorkSpec</code> — 29 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `Companion` | `static final bha` | `(mesmo nome)` |
| `SCHEDULE_NOT_REQUESTED_YET` | `static final long` | `(mesmo nome)` |
| `TAG` | `static final String` | `(mesmo nome)` |
| `WORK_INFO_MAPPER` | `static final vf3` | `(mesmo nome)` |
| `backOffOnSystemInterruptions` | `Boolean` | `(mesmo nome)` |
| `backoffDelayDuration` | `long` | `(mesmo nome)` |
| `backoffPolicy` | `BackoffPolicy` | `(mesmo nome)` |
| `constraints` | `tk1` | `(mesmo nome)` |
| `expedited` | `boolean` | `(mesmo nome)` |
| `flexDuration` | `long` | `(mesmo nome)` |
| `generation` | `int` | `next_schedule_time_override_generation` |
| `id` | `String` | `next_schedule_time_override_generation` |
| `initialDelay` | `long` | `(mesmo nome)` |
| `input` | `uv1` | `input_merger_class_name` |
| `inputMergerClassName` | `String` | `(mesmo nome)` |
| `intervalDuration` | `long` | `(mesmo nome)` |
| `lastEnqueueTime` | `long` | `(mesmo nome)` |
| `minimumRetentionDuration` | `long` | `(mesmo nome)` |
| `nextScheduleTimeOverride` | `long` | `(mesmo nome)` |
| `nextScheduleTimeOverrideGeneration` | `int` | `(mesmo nome)` |
| `outOfQuotaPolicy` | `OutOfQuotaPolicy` | `(mesmo nome)` |
| `output` | `uv1` | `output` |
| `periodCount` | `int` | `(mesmo nome)` |
| `runAttemptCount` | `int` | `(mesmo nome)` |
| `scheduleRequestedAt` | `long` | `(mesmo nome)` |
| `state` | `WorkInfo$State` | `state` |
| `stopReason` | `int` | `(mesmo nome)` |
| `traceTag` | `String` | `(mesmo nome)` |
| `workerClassName` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>WorkTag</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `tag` | `String` | `tag` |
| `workSpecId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>SystemIdInfo</code> — 3 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `generation` | `int` | `(mesmo nome)` |
| `systemId` | `int` | `(mesmo nome)` |
| `workSpecId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>WorkName</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `name` | `String` | `work_spec_id` |
| `workSpecId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>WorkProgress</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `progress` | `uv1` | `progress` |
| `workSpecId` | `String` | `(mesmo nome)` |

</details>

<details><summary><code>Preference</code> — 2 campos</summary>

| campo | tipo | coluna |
|---|---|---|
| `key` | `String` | `key` |
| `value` | `Long` | `long_value` |

</details>
