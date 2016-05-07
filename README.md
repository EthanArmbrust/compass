SAnd
====
Use your phones sensors (barometer and compass) to show your current orientation, height and air pressure.

important:
- showing height and air pressure only works, if your phone has a built-in barometer (if this is not the case it provides you a simple compass with orientation in degree)
- calculated height is based on [standard atmosphere](https://en.wikipedia.org/wiki/International_Standard_Atmosphere) and can be corrected within the app, if you know your real height


Notes
====
- application translated in English, French and German only
- code contains (many) bugs for sure and is not cleaned up yet


Feature Requests
===
=> [click here](https://gitlab.com/kas70/SAnd/issues/new)

Building
====
Should work with
```
git clone --recursive https://gitlab.com/kas70/SAnd.git
cd SAnd/
./gradlew build
```

License
====
SAnd is released under the terms of [GNU GPLv3](https://www.gnu.org/licenses/gpl-3.0.html).
