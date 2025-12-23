# アイゼンハワーマトリックス TODOリスト

Open Liberty上で動作する4象限TODOリスト管理アプリケーションです。重要度と緊急度でタスクを管理できます。

## 📋 概要

このアプリケーションは、アイゼンハワーマトリックス（重要度・緊急度マトリックス）を使用してタスクを4つの象限で管理します：

1. **🔥 重要 & 緊急** - すぐに対応すべきタスク
2. **📅 重要 & 緊急でない** - 計画的に取り組むタスク
3. **📞 重要でない & 緊急** - 委譲を検討すべきタスク
4. **🗑️ 重要でない & 緊急でない** - 削除を検討すべきタスク

## 🚀 技術スタック

- **サーバー**: Open Liberty 24.0.0.12
- **Java**: 21
- **Jakarta EE**: 10.0
- **MicroProfile**: 7.0
- **フロントエンド**: HTML5, CSS3, Vanilla JavaScript
- **ビルドツール**: Maven 3.x

## ✨ 機能

- ✅ タスクの作成・編集・削除
- ✅ 4象限でのタスク管理
- ✅ ドラッグ&ドロップでタスクを象限間で移動
- ✅ タスクの完了状態管理
- ✅ リアルタイムの統計表示
- ✅ レスポンシブデザイン（モバイル対応）
- ✅ REST API提供

## 📦 プロジェクト構造

```
bob-test/
├── src/
│   ├── main/
│   │   ├── java/com/demo/
│   │   │   ├── model/
│   │   │   │   └── Todo.java              # TODOエンティティ
│   │   │   ├── service/
│   │   │   │   └── TodoService.java       # ビジネスロジック
│   │   │   └── rest/
│   │   │       ├── RestApplication.java   # JAX-RS設定
│   │   │       └── TodoResource.java      # REST APIエンドポイント
│   │   ├── liberty/config/
│   │   │   └── server.xml                 # Liberty設定
│   │   └── webapp/
│   │       ├── index.html                 # メインページ
│   │       ├── css/
│   │       │   └── style.css             # スタイルシート
│   │       └── js/
│   │           └── app.js                # フロントエンドロジック
│   └── test/
└── pom.xml                                # Maven設定
```

## 🛠️ セットアップ

### 前提条件

- Java 21以上
- Maven 3.6以上

### インストール手順

1. **リポジトリのクローン**
   ```bash
   git clone <repository-url>
   cd bob-test
   ```

2. **ビルド**
   ```bash
   mvn clean package
   ```

3. **開発モードで起動**
   ```bash
   mvn liberty:dev
   ```

   サーバーが起動すると、以下のメッセージが表示されます：
   ```
   Liberty server HTTP port: [ 9080 ]
   Liberty server HTTPS port: [ 9443 ]
   ```

4. **アプリケーションにアクセス**
   
   ブラウザで以下のURLを開きます：
   ```
   http://localhost:9080/bob-test/
   ```

## 🔌 REST API

### エンドポイント一覧

| メソッド | パス | 説明 |
|---------|------|------|
| GET | `/api/todos` | 全TODOリストを取得 |
| GET | `/api/todos/{id}` | 特定のTODOを取得 |
| GET | `/api/todos/quadrant/{quadrant}` | 象限別にTODOを取得 (1-4) |
| GET | `/api/todos/stats` | 統計情報を取得 |
| POST | `/api/todos` | 新規TODO作成 |
| PUT | `/api/todos/{id}` | TODO更新 |
| PATCH | `/api/todos/{id}/toggle` | 完了状態を切り替え |
| DELETE | `/api/todos/{id}` | TODO削除 |

### リクエスト例

**新規TODO作成**
```bash
curl -X POST http://localhost:9080/bob-test/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "重要なタスク",
    "description": "詳細な説明",
    "important": true,
    "urgent": true,
    "completed": false
  }'
```

**全TODO取得**
```bash
curl http://localhost:9080/bob-test/api/todos
```

**象限別取得**
```bash
curl http://localhost:9080/bob-test/api/todos/quadrant/1
```

## 📱 使い方

### タスクの作成
1. 「+ 新しいタスク」ボタンをクリック
2. タスク名を入力（必須）
3. 説明を入力（任意）
4. 重要度と緊急度をチェックボックスで選択
5. 「保存」をクリック

### タスクの移動
- タスクカードをドラッグ&ドロップで別の象限に移動
- 移動すると自動的に重要度・緊急度が更新されます

### タスクの編集
- タスクカードの✏️アイコンをクリック
- 内容を編集して「保存」

### タスクの削除
- タスクカードの🗑️アイコンをクリック
- 確認ダイアログで「OK」

### タスクの完了
- タスクカードのチェックボックスをクリック
- 完了したタスクは半透明で表示されます

## 🔧 開発

### 開発モードの機能

`mvn liberty:dev`で起動すると、以下の機能が利用できます：

- **ホットリロード**: ソースコードの変更を自動検知して再デプロイ
- **デバッグ**: ポート7777でデバッガーを接続可能
- **対話モード**: 
  - `h` - ヘルプメニュー表示
  - `q` - サーバー停止

### サーバーの停止

開発モードの場合：
```bash
# ターミナルで 'q' を入力するか Ctrl+C
```

通常モードの場合：
```bash
mvn liberty:stop
```

## 📊 データ永続化

現在のバージョンでは、データはメモリ内（ConcurrentHashMap）に保存されます。
サーバーを再起動すると、データは初期化されます。

サンプルデータが自動的に作成されます：
- 緊急の顧客対応（重要 & 緊急）
- プロジェクト計画（重要 & 緊急でない）
- 会議の準備（重要でない & 緊急）
- メールチェック（重要でない & 緊急でない）

## 🎨 カスタマイズ

### 象限の色を変更

`src/main/webapp/css/style.css`の以下の変数を編集：

```css
:root {
    --quadrant-1: #ff6b6b;  /* 重要 & 緊急 */
    --quadrant-2: #4ecdc4;  /* 重要 & 緊急でない */
    --quadrant-3: #ffe66d;  /* 重要でない & 緊急 */
    --quadrant-4: #95e1d3;  /* 重要でない & 緊急でない */
}
```

### サーバーポートの変更

`src/main/liberty/config/server.xml`を編集：

```xml
<httpEndpoint id="defaultHttpEndpoint"
              httpPort="9080"
              httpsPort="9443" />
```

## 🐛 トラブルシューティング

### ポートが既に使用されている

```bash
# サーバーを停止
mvn liberty:stop

# ポートを使用しているプロセスを確認（macOS/Linux）
lsof -i :9080

# プロセスを終了
kill -9 <PID>
```

### ビルドエラー

```bash
# クリーンビルド
mvn clean install

# 依存関係の更新
mvn dependency:resolve
```

## 📝 ライセンス

このプロジェクトはサンプルアプリケーションです。


## 📧 お問い合わせ

質問や提案がある場合は、issueを作成してください。

---

**Enjoy managing your tasks with the Eisenhower Matrix! 🎯**