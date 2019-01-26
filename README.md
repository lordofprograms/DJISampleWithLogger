# DJISampleWithLogger
Add Logger to [DJI Sample Code Android App](https://github.com/dji-sdk/Mobile-SDK-Android/) to get location and aircraft principle axes.
Logger write data using [FlightController callback](https://developer.dji.com/mobile-sdk/documentation/cn/faq/cn/api-reference/android-api/Components/FlightController/DJIFlightController.html#djiflightcontroller_setupdatesystemstatecallback_inline) per second to csv file in format(Date, time, lat, lon, alt, pitch, roll, yaw).
Logger realization used Singelton pattern and called CsvLogger and located in utils.
CsvLogger is called by DemoListView on 175 code line.