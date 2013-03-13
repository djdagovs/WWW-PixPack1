#!/bin/sh

#JPG
for image in *.jpg; do
	echo "Processing: ${image}"
	convert -verbose ${image}[0] -depth 8 -quality 75 -trim -thumbnail x120 -resize '160x<' -gravity Center -repage 160x120 -crop 160x120 _${image}
done

#PNG
for image in *.png; do
	echo "Processing: ${image}"
	convert -verbose ${image}[0] -depth 8 -quality 75 -trim -thumbnail x120 -resize '160x<' -gravity Center -repage 160x120 -crop 160x120 _${image}
done

#GIF
for image in *.gif; do
	echo "Processing: ${image}"
	convert -verbose ${image}[0] -depth 8 -quality 75 -trim -thumbnail x120 -resize '160x<' -gravity Center -repage 160x120 -crop 160x120 _${image}
done

