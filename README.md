# Impergram - a slick experimental client based on [TelegramX](https://github.com/TGX-Android/Telegram-X) and [TDLib](https://core.telegram.org/tdlib).

fork [TelegramX](https://github.com/TGX-Android/Telegram-X)

## License

* [GNU GPL V.3](LICENSE)

## Author Impergram

* [Gleb Obitotsky](https://t.me/oxxx1mif) || @oxxx1mif or @obitotskyg

## Build instructions

### Prerequisites

* At least **5,34GB** of free disk space: **487,10MB** for source codes and around **4,85GB** for files generated after building all variants
* **4GB** of RAM
* **macOS** or **Linux**-based operating system.

#### macOS

* [Homebrew](https://brew.sh)
* git with LFS, wget and sed: `$ brew install git git-lfs wget gsed && git lfs install`

#### Ubuntu

* git with LFS: `# apt install git git-lfs`
* Run `$ git lfs install` for the current user, if you didn't have `git-lfs` previously installed

#### Windows

* **Impergram** does not provide official build instructions for Windows platform. It is recommended to rely on Linux distributions instead.

### Building

1. `$ git clone --recursive --depth=1 --shallow-submodules https://github.com/oxxx1mif/impergram-x.git tgx` — clone **Impergram** with submodules
2. In case you forgot the `--recursive` flag, `cd` into `tgx` directory and: `$ git submodule init && git submodule update --init --recursive --depth=1`
3. Create `keystore.properties` file outside of source tree with the following properties:<br/>`keystore.file`: absolute path to the keystore file<br/>`keystore.password`: password for the keystore<br/>`key.alias`: key alias that will be used to sign the app<br/>`key.password`: key password.<br/>**Warning**: keep this file safe and make sure nobody, except you, has access to it. For production builds one could use a separate user with home folder encryption to avoid harm from physical theft
4. `$ cd tgx`
5. Run `$ scripts/./setup.sh` and follow up the instructions
6. If you specified package name that's different from the one Impergram uses, [setup Firebase](https://firebase.google.com/docs/android/setup) and replace `google-services.json` with the one that's suitable for the `app.id` you need
7. Now you can open the project using **[Android Studio](https://developer.android.com/studio/)** or build manually from the command line: `./gradlew assembleUniversalRelease`.