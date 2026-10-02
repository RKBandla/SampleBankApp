# Deploying Sample Bank to AWS

```
Browser ──► S3 static website (React)
              │  fetch https://<api-id>.execute-api.<region>.amazonaws.com/api/...
              ▼
        API Gateway (HTTP API)  ──►  AWS Lambda (Spring Boot, Java 17)  ──►  MongoDB Atlas
```

- **Frontend:** the React build (`dist/`) is uploaded to an **S3 bucket** with *static website hosting*.
- **Backend:** the same Spring Boot code runs inside **Lambda**. `StreamLambdaHandler` starts Spring Boot and passes every API Gateway request to the normal controllers.
- **Database:** MongoDB Atlas stays the same. Lambda reads the connection string from an **environment variable**.

Use the **same AWS region** for everything (e.g. `us-east-1`).

---

## Step 1: MongoDB Atlas, allow Lambda to connect

Lambda doesn't have a fixed IP address, so in Atlas go to **Security → Network Access → Add IP Address → Allow access from anywhere (`0.0.0.0/0`) → Confirm**.

Keep your connection string handy: `mongodb+srv://user:password@cluster0.xxxxx.mongodb.net/`

---

## Step 2: Build the Lambda zip

**IntelliJ (no Maven install needed):**
1. Open the **Maven** panel (right edge, the *m* icon).
2. Under **Profiles**, tick **lambda**.
3. Click the **"Skip tests"** button (the ⊘ icon at the top of the panel).
4. Under **Lifecycle**, double-click **clean**, then **package**.

**Or in a terminal** (if Maven is installed): `mvn clean package -P lambda -DskipTests`

Result: **`target/bankapp-java-api-backend-0.0.1-SNAPSHOT-lambda.zip`**

Untick the **lambda** profile afterwards, so normal local runs are unaffected.

> The zip contains the compiled classes at the root and every library in `lib/`, the layout Lambda's Java runtime expects.
> If the zip is **bigger than 50 MB**, upload it to an S3 bucket first and point Lambda at it (see step 3.2).

---

## Step 3: Create the Lambda function

1. AWS Console → **Lambda** → **Create function** → *Author from scratch*
   - Function name: `samplebank-api`
   - Runtime: **Java 17**
   - Architecture: x86_64
   - **Create function**
2. **Code** tab → **Upload from** → **.zip file** → choose the zip from step 2 → **Save**.
   (Over 50 MB: upload the zip to any S3 bucket, then **Upload from → Amazon S3 location** and paste its *Object URL*.)
3. **Code** tab → scroll to **Runtime settings** → **Edit** → Handler:
   ```
   com.example.demo.StreamLambdaHandler::handleRequest
   ```
4. **Configuration → General configuration → Edit**
   - Memory: **2048 MB** (more memory = more CPU = faster Spring Boot start)
   - Timeout: **1 min**
5. **Configuration → Environment variables → Edit → Add**:

   | Key | Value |
   |---|---|
   | `MONGODB_URI` | your Atlas connection string |
   | `JWT_SECRET` | any random text, 32+ characters |
   | `ADMIN_PASSWORD` | your admin password (optional, default `Admin@123`) |

6. *(Optional, makes the first request much faster)* **Configuration → General configuration → SnapStart → PublishedVersions**. Then **Actions → Publish new version**, and in step 4 choose that **version** (or an alias pointing to it) as the integration target.

---

## Step 4: Create the API Gateway (HTTP API)

1. AWS Console → **API Gateway** → **Create API** → **HTTP API** → **Build**
2. **Add integration** → **Lambda** → choose `samplebank-api` (version **2.0**). API name: `samplebank-http-api` → **Next**
3. **Configure routes**: Method **ANY**, Resource path **`/{proxy+}`**, Integration target `samplebank-api` → **Next**
4. **Stages**: keep **`$default`** with *Auto-deploy* on → **Next** → **Create**
5. Copy the **Invoke URL**, e.g. `https://abc123xyz.execute-api.us-east-1.amazonaws.com`
6. Left menu **CORS** → **Configure**:
   - Access-Control-Allow-Origin: `*` (later you can replace it with your S3 website URL)
   - Access-Control-Allow-Headers: `content-type, authorization`
   - Access-Control-Allow-Methods: `GET, POST, PUT, DELETE, OPTIONS`
   - **Save**

**Test it:** open `<Invoke URL>/api/customers` in the browser. You should get
`{"error":"Unauthorized - missing or invalid token"}`. That means Lambda, Spring Boot and security all work.
The **first** call after a while can take 10–20 seconds (a "cold start"); refresh if it times out.

---

## Step 5: Build the frontend for AWS

In Git Bash:

```bash
cd bankapp-react-frontend
cp .env.production.example .env.production
```

Edit `.env.production` and put your Invoke URL (no `/` at the end):

```
VITE_API_BASE_URL=https://abc123xyz.execute-api.us-east-1.amazonaws.com
```

Then:

```bash
npm install
npm run build
```

This creates the **`dist/`** folder (`index.html` + `assets/`).

---

## Step 6: Host the frontend on S3

1. AWS Console → **S3** → **Create bucket**
   - Name: something unique, e.g. `samplebank-rohan-frontend`
   - **Untick "Block all public access"** and tick the acknowledgement
   - **Create bucket**
2. Open the bucket → **Upload** → drag in **the contents of `dist/`** (`index.html` and the `assets` folder, not the `dist` folder itself) → **Upload**
3. **Properties** → **Static website hosting** → **Edit** → Enable
   - Index document: `index.html`
   - Error document: `index.html`
   - **Save**
4. **Permissions** → **Bucket policy** → **Edit** → paste (replace the bucket name) → **Save**:
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Sid": "PublicReadForWebsite",
         "Effect": "Allow",
         "Principal": "*",
         "Action": "s3:GetObject",
         "Resource": "arn:aws:s3:::samplebank-rohan-frontend/*"
       }
     ]
   }
   ```
5. **Properties** → **Static website hosting**: copy the **Bucket website endpoint**:
   ```
   http://samplebank-rohan-frontend.s3-website-us-east-1.amazonaws.com
   ```
   **This is your deployed project link.**

---

## Updating later

| Changed | Do this |
|---|---|
| Backend code | Rebuild the zip (step 2) → Lambda → **Upload from → .zip file** |
| Frontend code | `npm run build` → upload the new `dist/` contents to the bucket (replace files) |
| API URL | Edit `.env.production` → `npm run build` → upload again |

---

## Troubleshooting

| Problem | Fix |
|---|---|
| S3 link shows **403 Forbidden / AccessDenied** | Public access still blocked, or the bucket policy is missing/has the wrong bucket name |
| App says **"Cannot reach the server"** | `VITE_API_BASE_URL` wrong or missing → fix `.env.production`, rebuild, re-upload |
| Browser console shows a **CORS** error | Step 4.6: check `authorization` and `content-type` are allowed headers |
| `{"message":"Internal Server Error"}` from the API | Lambda → **Monitor** → **View CloudWatch logs** → read the error (usually `MONGODB_URI` missing or a wrong handler name) |
| `{"message":"Service Unavailable"}` / timeout on first call | Cold start took too long → refresh; raise memory to 3008 MB or enable SnapStart |
| `ClassNotFoundException: com.example.demo.StreamLambdaHandler` | Handler name typo (step 3.3), or you uploaded the normal jar instead of the **-lambda.zip** |
| MongoDB timeout in the logs | Atlas Network Access must include `0.0.0.0/0` |

## Cost

Everything here fits in the AWS Free Tier for a class project (Lambda, API Gateway HTTP API and S3 have free monthly allowances). Delete the bucket, API and function when you no longer need them.
