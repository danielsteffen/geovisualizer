if %1!==! goto :ende
echo Rasterizing
gdal_rasterize -burn 0 -burn 0 -burn 0 -burn 0 -l "Air Quality Rooftop" -ot Byte -ts 700 700 "Air Quality Rooftop.shp" %1
gdal_rasterize -b 1 -b 2 -b 3 -b 4 -burn 255 -burn 0 -burn 0 -burn 255 -where "NO2dygn>60.0" -l "Air Quality Rooftop" "Air Quality Rooftop.shp" %1
gdal_rasterize -b 1 -b 2 -b 3 -b 4 -burn 255 -burn 165 -burn 0 -burn 255 -where "NO2dygn<60.0" -l "Air Quality Rooftop" "Air Quality Rooftop.shp" %1
gdal_rasterize -b 1 -b 2 -b 3 -b 4 -burn 255 -burn 255 -burn 0 -burn 255 -where "NO2dygn<50.0" -l "Air Quality Rooftop" "Air Quality Rooftop.shp" %1
gdal_rasterize -b 1 -b 2 -b 3 -b 4 -burn 173 -burn 255 -burn 47 -burn 255 -where "NO2dygn<40.0" -l "Air Quality Rooftop" "Air Quality Rooftop.shp" %1
gdal_rasterize -b 1 -b 2 -b 3 -b 4 -burn 0 -burn 255 -burn 0 -burn 255 -where "NO2dygn<30.0" -l "Air Quality Rooftop" "Air Quality Rooftop.shp" %1
gdal_rasterize -b 1 -b 2 -b 3 -b 4 -burn 0 -burn 0 -burn 255 -burn 255 -where "NO2dygn<25.0" -l "Air Quality Rooftop" "Air Quality Rooftop.shp" %1
:ende