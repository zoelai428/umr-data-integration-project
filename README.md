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

- `0_datasets`: This directory should contain all the required datasets with our predefined filenames. 
  Details can be found in "**Step 0: Datasets**" section below.
- `1_preparation`: This directory contains our drafted ER-Model, slides for our presentation
  and our first draft of `Main.java`. `Main.java` aims to read all the datasets 
  correctly according to our preferred format. Details can be found in "**Step 1: Preparation**" section below.
- `2_integration`: [to be edited] Experiments to integrate your datasets and a program that
  implements your final integration pipeline.
- `3_cleaning`: [to be edited] Experiments to clean your integrated dataset and a program that
  implements your final data cleaning pipeline.
- `3_showcase`: [to be edited] Code to implement your showcase. You can already run it against
  the results of your data integration pipeline, and, finally, against your
integrated and cleaned data.

## Step 0: Datasets

There are 2 main categories of datasets that are applied in this project: Bigfoot and UFO.
For convenience, all of these datasets can be downloaded from
[here](https://hessenbox.uni-marburg.de/dl/fiHf8BF7GjgyhXviHqyJzE/datasets.dir) with the correct predefined filenames,
whereas the following documents their original sources with respective links.

*About Bigfoot*

- `bigfoot1_reports.csv`: Original source from Kaggle with link 
[here](https://www.kaggle.com/datasets/josephvm/bigfoot-sightings-data).

- `bigfoot2_bfro_locations.csv`, `bigfoot2_bfro_reports.json` and `bigfoot2_bfro_reports_geocoded.csv`: 
Original source from Timothy Renner on data.world with link [here](https://data.world/timothyrenner/bfro-sightings-data).

- `bigfoot3_Bigfoot_Sightings.csv`: Original source from ArcGIS with link 
[here](https://hub.arcgis.com/datasets/d0afc5b29e4346cc9a4cf8e43bcaaed0_0/explore?location=32.184092%2C-115.796850%2C3.88).

- `bigfoot4_DataDNA_Dataset_Challenge-February_2023.xlsx`: Original source from Kaggle with link 
[here](https://www.kaggle.com/datasets/sridharstreaks/datadna-dataset-challenge-feb-bigfoot-sightings).

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

## Step 1: Preparation

After all the required datasets are saved under the directory `0_datasets`, they are then loaded to our format as 
`List<String[]>` in `Main.java`, so that the first array contains the headings and the remaining arrays contain the records.

Several functions are defined so as to accomodate different file types. They include: 
- `read_csv_file(String filename)` for loading csv files,
- `read_json_file(String filename, String[] header)` for loading json files, and
- `read_xlsx_file(String filename)` for loading xlsx files.
