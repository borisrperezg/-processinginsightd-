## Procesing InsighTD: Repository of tools used

**Goal**

The goal of this repository is shared the tools used to characterize the TD repayment strategies used in Brazil, Chile, Colombia, and the United States, as a part of [InsighTD Project](http://td-survey.com/). Analyses of the four countries were made on a consolidated basis, and on an individual basis. An association was made between TD causes and repayment strategies in order to find out if certain causes implied certain payment strategies.

**Credits**  

RBO implementation used in this project was taken from ÚČNK/David Lukeš: "A small Python module for calculating rank-biased overlap, a measure of similarity between ragged, possibly infinite ranked lists which may or may not contain the same items (up to the actually evaluated depth or at all). See "A similarity measure for indefinite rankings" by W. Webber, A. Moffat and J. Zobel (2011), http://dx.doi.org/10.1145/1852102.1852106."

- [RBO Implementation](https://github.com/dlukes/rbo)

**Directories Structure**

- AnalysisFiles: This folder contains consolidated data about InsighTD survey results. This information was taken from Excel files and processed using an Eclipse project. This data is presented to go deeper in any revision of content.
- EclipseProjects: Actuallly, this folder only store one project. This project took the raw answers and begin to extract relevant information. This project use answers from Brazil, Chile, Colombia and the United States. Output from this project are the consolidated files stores in the folder AnalysisFiles.
- Notebooks: This folder store the Jupyter Notebooks used in this project to get a Radar chart and a Heatmap. Radar chart was used to present the distribution of practices on TD repayment among all four countries. Heatmap was used to present the association between main causes leading to TD ocurrence and the practices used on TD repayment.
