# Quiz

Android Quiz that uses an API to store and retrieves data.

## Getting Started

### Prerequisites

Install PHP : https://windows.php.net/download/
Set the PATH environment variable to your php folder.
Open `php.ini` file and allow the php_gd2 extension by uncommenting the line

```
extension=php_gd2.dll
```

### Installing

Start the api in the `api` folder

```
$ php -S 0.0.0.0:8080
```

Find your IP with `ipconfig` command and set `API_URL` in the `build.gradle` file.

```
buildConfigField 'String', 'API_URL', '"http://your_ip:8080"'
```

Then, click on `Sync Now` and start the app.

### Documentation

In Android Studio, click on `Tools` > `Generate JavaDoc...`.

Select `Custom scope` and chose `Project Files`
Uncheck `Include test sources`.

For the `Output directory` browse the `docs` folder
Set the cursor to `private`.

Check `Open generated documentation in browser`

Find the documentation in `docs/index.html`

## Running the tests

Right click the test folder and click on `Run`.
Or click on test folder and press `Ctrl+Maj+F10`
