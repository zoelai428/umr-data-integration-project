# Data Integration Project: Is Bigfoot an Alien?

Do you believe in aliens? Have you heard of unexplainable objects that people saw on Earth? 
Here we have gathered multiple datasets which have recorded appearances of UFOs and Bigfoot.
We want to answer some of our questions, for example, if there is any pattern when and where
UFO and Bigfoot appear.
Using data integration techniques, we will try to find some interesting insights and 
hopefully answers to our questions.

## Structure

This data integration project has four components: preparation, data
integration, data cleaning, and showcase.

- `0_datasets`: Should contain all the datasets with the predefined filenames. 
  They can be downloaded [here](https://hessenbox.uni-marburg.de/dl/fi9vhEKt4uthdwRWTSmw5V/.dir). 
  Details of the original sources can be found in `Datasets` section below.
- `1_preparation`: [to be edited] Code to extract and load your datasets into a common space
  (e.g., sqlite or Jupyter notebook). You might also want to write code to explore the
datasets.
- `2_integration`: [to be edited] Experiments to integrate your datasets and a program that
  implements your final integration pipeline.
- `3_cleaning`: [to be edited] Experiments to clean your integrated dataset and a program that
  implements your final data cleaning pipeline.
- `3_showcase`: [to be edited] Code to implement your showcase. You can already run it against
  the results of your data integration pipeline, and, finally, against your
integrated and cleaned data.

## Datasets

There are 2 main categories of datasets that are applied in this project: Bigfoot and UFO.
The following documents the original sources of these datasets:

*About Bigfoot*

- `bigfoot1_reports.csv`: Original source from Kaggle with link 
[here](https://www.kaggle.com/datasets/josephvm/bigfoot-sightings-data).

- `bigfoot2_bfro_locations.csv`, `bigfoot2_bfro_reports.json` and `bigfoot2_bfro_reports_geocoded.csv`: 
Original source from Timothy Renner on data.world with link [here](https://data.world/timothyrenner/bfro-sightings-data).

- `bigfoot3_Bigfoot_Sightings.csv`: Original source from ArcGIS with link 
[here](https://hub.arcgis.com/datasets/d0afc5b29e4346cc9a4cf8e43bcaaed0_0/explore?location=32.184092%2C-115.796850%2C3.88)

- `bigfoot4_DataDNA_Dataset_Challenge-February_2023.xlsx`: Original source from Kaggle with link [here](https://www.kaggle.com/datasets/chemcnabb/bfro-bigfoot-sighting-report).

*About UFO*

- `ufo1_nuforc_reports.csv`, `ufo1_nuforc_reports.json`:
  Original source from Timothy Renner on data.world with link
  [here](https://data.world/timothyrenner/ufo-sightings).

- `ufo2_ufo_sighting_data.csv`
  Original source from Kaggle with link
  [here](https://www.kaggle.com/datasets/camnugent/ufo-sightings-around-the-world).

- `ufo3_nuforc_reports.csv`
  Original source from Kaggle with link
  [here](https://www.kaggle.com/datasets/thedevastator/uncovering-mysterious-unexplained-ufo-sightings).

## [to be edited]Documentation

All code documentation and instructions should be placed in this `README.md`;
feel free to erase this intro text.
