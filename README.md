# Hasura Android-SDK
#### Adding Top-level build.gradle configuration
```
apply plugin: 'io.hasura.plugin.hasuraplugin'
buildscript {

   repositories {
       jcenter()
       maven { url "https://jitpack.io" }
   }

   dependencies {
       classpath 'com.android.tools.build:gradle:2.2.0-beta2'
       classpath 'com.github.hasura.android-sdk:build:0.1.0-alpha2'
   }

}

allprojects {

   repositories {
       jcenter()
       maven { url "https://jitpack.io" }
   }

}
```

Add the plugin in your gradle file for db-code generation<br/>
`apply plugin: 'io.hasura.plugin.hasuraplugin'`

In the buildscript dependencies add the following classpath for the Hasura SDK<br/>
`classpath 'com.github.hasura.android-sdk:build:0.1.0-alpha2'`

#### Adding App build.gradle configuration

```
apply plugin: 'com.android.application'
android {
   compileSdkVersion 23
   buildToolsVersion "23.0.3"

   defaultConfig {
       applicationId "io.hasura.todo_sdk_test"
       minSdkVersion 14
       targetSdkVersion 23
       versionCode 1
       versionName "1.0"
   }

   buildTypes {
       release {
           minifyEnabled false
           proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
       }
   }

}

repositories{
   jcenter()
   maven { url "https://jitpack.io" }
}

dependencies {
   compile fileTree(dir: 'libs', include: ['*.jar'])
   androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
       exclude group: 'com.android.support', module: 'support-annotations'
   })
   compile 'com.squareup.okhttp3:okhttp:3.2.0'
}
```

Add the jitpack repository link in your app build.gradle
```
repositories{
   jcenter()
   maven { url "https://jitpack.io" }
}
```
Add dependency library
`compile 'com.squareup.okhttp3:okhttp:3.2.0' `

### Adding Hasura properties
Add hasura.properties in your project root folder in parallel with build.gradle

<img src="https://github.com/hasura/android-sdk/blob/master/docs/Screen%20Shot%202016-09-01%20at%201.48.08%20PM.png?raw=true" align="left" height="250" width="200" />
<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
In you _**hasura.properties**_ file include the following
```
project.name=nonslip53
admin.token=twoncmyxbxuywl917s9sbyby32v6nk0q
```

<img src="https://raw.githubusercontent.com/hasura/android-sdk/master/docs/Screen%20Shot%202016-09-01%20at%201.53.45%20PM.png?raw=true" align="left" height="300" width="800" /><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
You can get the project name and admin token from Hasura web console.
We have added `nonslip53` project for demo purpose, you can also include you project name and admin token.

After adding the above dependencies to the project press gradle-sync
<img src="https://raw.githubusercontent.com/hasura/android-sdk/master/docs/Screen%20Shot%202016-08-30%20at%207.36.34%20PM.png?raw=true"  height="20" width="20" />
.After gradle sync you will see a new task in gradle side panel and db-code will not be generated until run the new task generated.


### Code Generation Procedure

<img src="https://raw.githubusercontent.com/hasura/android-sdk/master/docs/Screen%20Shot%202016-09-01%20at%203.44.48%20PM.png?raw=true"  height="250" width="200" />
<br/>

From the gradle side panel `double click codegen`  it will generate the db service code in as a library module inside project structure.In our case its `nonslip53`

_Note: If your codgen happens to fails with the following error as below_

<img src="https://github.com/hasura/android-sdk/blob/master/docs/error.png?raw=true"  height="150" width="350" /><br/>

_Or with this_

<img src="https://github.com/hasura/android-sdk/blob/master/docs/terminal-error.png?raw=true"  height="200" width="500" /><br/>

Then you may need to install some additional system certificate to generate db-code
[certificates](https://community.letsencrypt.org/t/ssl-certs-in-java/15450)

Please install letsencrypt certificate for java in your system then do codegen.
Successful codegen will give you an output log like this.

<img src="https://github.com/hasura/android-sdk/blob/master/docs/success-build.png?raw=true"  height="150" width="350" /><br/>

<img src="https://github.com/hasura/android-sdk/blob/master/docs/Screen%20Shot%202016-09-01%20at%203.46.20%20PM.png?raw=true"  height="250" width="200" /><br/>

### After code-generation
In `settings.gradle` include the db module along with app

```
include 'nonslip53'
```

#### In app build.gradle
```
apply plugin: 'com.android.application'
android {
   compileSdkVersion 23
   buildToolsVersion "23.0.3"
   defaultConfig {
       applicationId "io.hasura.todo_sdk_test"
       minSdkVersion 14
       targetSdkVersion 23
       versionCode 1
       versionName "1.0"
   }
   buildTypes {
       release {
           minifyEnabled false
           proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
       }
   }
}

repositories{
   jcenter()
   maven { url "https://jitpack.io" }
}

dependencies {
   compile fileTree(dir: 'libs', include: ['*.jar'])
   compile 'com.squareup.okhttp3:okhttp:3.2.0'
   compile project(':nonslip53')
}
```
Do gradle sync once again.

#### Finally configure Hasura in your application class
```
public class TodoApplication extends Application {
   @Override
   public void onCreate() {
       super.onCreate();
String projectName = "nonslip53";
String authUrl = "https://auth." + projectName + ".hasura-app.io";
String dbUrl = "https://data." + projectName + ".hasura-app.io/api/1";
Hasura.init(getApplicationContext(), authUrl, dbUrl);
   }
}
```
Where the first argument is the Application Context. Second is the Auth url  followed by DB url.


#### How to get DB url

<img src="https://github.com/hasura/android-sdk/blob/master/docs/Screen%20Shot%202016-09-01%20at%204.42.54%20PM.png?raw=true" align="left" height="300" width="800" /><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

#### How to get Auth url


<img src="https://github.com/hasura/android-sdk/blob/master/docs/Screen%20Shot%202016-09-01%20at%204.43.06%20PM.png?raw=true" align="left" height="300" width="800" /><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>

Your Hasura android-sdk integration is now complete.
Run the application now.
