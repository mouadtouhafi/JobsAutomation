package collectjobs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpleoJobCollector {
    private static final Logger logger = Logger.getLogger(ExpleoJobCollector.class.getName());
    CompaniesLinks companiesLinks = new CompaniesLinks();

    private final HashMap<Integer, List<String>> id_jobInfo = new HashMap<>();
    private final HashMap<Integer, StringBuilder> id_jobQualifications = new HashMap<>();
    private final HashMap<Integer, StringBuilder> id_jobMissions = new HashMap<>();

    private final HashMap<Integer, String> jobsLinks = new HashMap<>();

    private boolean isFinalPageReached = false;

    public HashMap<Integer, String> getJobsLinks() {
        return jobsLinks;
    }

    public HashMap<Integer, StringBuilder> getId_jobMissions() {
        return id_jobMissions;
    }

    public HashMap<Integer, StringBuilder> getId_jobQualifications() {
        return id_jobQualifications;
    }

    WebDriver driver = new EdgeDriver();

    public HashMap<Integer, List<String>> getId_jobInfo() {
        return id_jobInfo;
    }

    public void setUpDriver(){
        String link = companiesLinks.expleoLink;
        driver.get(link);
    }

    public void closeDriver(){
        driver.quit();
    }

    public void getJobsInformations(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            /*
            *  The globalIndex will be used as the id for each job, because we will iterate over page
            *  and using only the for loop index as id will not work because in each page the id will
            *  be re-initialized.
            *
            *  The page count indicates simply the page number.
            * */
            int globalIndex = 0;
            int pageCount = 1;

            while(!isFinalPageReached) {

                logger.info("We are now processing the page number : " + pageCount++);
                /*
                 * Here we wait for the 5th iframe (index 4) to be available and automatically switch into it
                 * This 4th iframe contains the dynamically loaded job listings.
                 */
                driver.switchTo().defaultContent();
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(4));

                WebElement jobTable;
                jobTable = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("div.container-fluid.iCIMS_JobsTable")
                        )
                );
                List<WebElement> rows = jobTable.findElements(By.className("row"));

                for (WebElement row : rows) {
                    //System.out.println("Row " + (i + 1) + ": " + rows.get(i).getText());
                    WebElement link = row.findElement(By.cssSelector("div.col-xs-12.title a.iCIMS_Anchor"));
                    WebElement job = row.findElement(By.cssSelector("div.col-xs-12.title a.iCIMS_Anchor h3"));

                    String jobLink = link.getAttribute("href");
                    String jobTitle = job.getText();


                    /*
                     * Each job bloc contains a DIV which has infos such "Location", "work mode", "contract type"
                     * We get that block which we named as "InfosDivs", after this, we iterate over its DIVs.
                     * Each DIV has two child tags <dt> and <dd>.
                     * */

                    List<WebElement> InfosDivs = row.findElements(By.cssSelector("div.col-xs-12.additionalFields dl.iCIMS_JobHeaderGroup div"));
                    String location = "";
                    String contractType = "";
                    String workMode = "";
                    for (WebElement element : InfosDivs) {
                        WebElement dt_element = element.findElement(By.tagName("dt"));
                        WebElement dd_element = element.findElement(By.tagName("dd"));

                        String name = dt_element.getText();
                        String value = dd_element.getText();


                        switch (name.toLowerCase().stripLeading().stripTrailing()) {
                            case "job locations":
                                location = value;
                            case "type d’emploi":
                                contractType = value;
                            case "lieu de travail":
                                workMode = value;
                            default:
                                break;
                        }
                    }

                    List<String> infos = new ArrayList<>();
                    infos.add(jobTitle);
                    infos.add(location);
                    infos.add(contractType);
                    infos.add(workMode);

                    id_jobInfo.put(globalIndex, infos);
                    jobsLinks.put(globalIndex, jobLink);
                    globalIndex++;
                }


                /*
                 *  Here in this section, we will try to find and click the nextPage button.
                 * */

                try {
                    WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("div.iCIMS_Paging.text-center span.halflings.halflings-menu-right")
                    ));

                    /*  Here we check if next button is invisible, if yes then we reached the final page.  */
                    String classAttr = nextButton.getAttribute("class");
                    if ((classAttr != null && classAttr.contains("invisible")) ||
                        (classAttr != null && classAttr.contains("disabled"))) {
                        logger.info("Reached last page");
                        isFinalPageReached = true;
                        break;
                    }
                    WebElement firstRow = rows.getFirst();
                    nextButton.click();

                    wait.until(ExpectedConditions.stalenessOf(firstRow));
                } catch (Exception e) {
                    logger.info("No more pages available");
                    isFinalPageReached = true;
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        }
    }

    public void getJobsRequirements(){
        for(int i=0; i<jobsLinks.size(); i++){
            driver.get(jobsLinks.get(i));
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                WebElement iframe = driver.findElement(By.id("icims_content_iframe"));
                driver.switchTo().frame(iframe);

                try {
                    List<WebElement> dataDiv = wait.until(
                            ExpectedConditions.visibilityOfAllElementsLocatedBy(
                                    By.cssSelector("div.iCIMS_JobContainer div.iCIMS_Expandable_Text")
                            )
                    );

                    List<WebElement> missionsList = dataDiv.get(1).findElements(By.tagName("li"));
                    System.out.println(missionsList.size());
                    StringBuilder missionsStr = new StringBuilder();
                    if(!missionsList.isEmpty()){
                        for (WebElement webElement : missionsList) {
                            String s = webElement.getText();
                            if(!s.isEmpty() && !s.contains("Le Groupe EXPLEO s’appuie sur 19000")){
                                missionsStr.append("- ").append(webElement.getText());
                            }
                        }
                    }else{
                        missionsList = dataDiv.get(1).findElements(By.tagName("div"));
                        if(!missionsList.isEmpty()){
                            for (WebElement webElement : missionsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty() && !s.contains("Le Groupe EXPLEO s’appuie sur 19000")){
                                    missionsStr.append("- ").append(webElement.getText());
                                }
                            }
                        }else {
                            missionsList = dataDiv.get(1).findElements(By.tagName("p"));
                            for (WebElement webElement : missionsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty() && !s.contains("Le Groupe EXPLEO s’appuie sur 19000")){
                                    missionsStr.append("- ").append(webElement.getText());
                                }
                            }
                        }
                    }

                    List<WebElement> qualificationsList = dataDiv.get(2).findElements(By.tagName("li"));
                    StringBuilder qualificationsStr = new StringBuilder();
                    if(!qualificationsList.isEmpty()){
                        for (WebElement webElement : qualificationsList) {
                            String s = webElement.getText();
                            if(!s.isEmpty() && !s.contains("Le Groupe EXPLEO s’appuie sur 19000")){
                                qualificationsStr.append("- ").append(webElement.getText());
                            }
                        }
                    }else{
                        qualificationsList = dataDiv.get(2).findElements(By.tagName("div"));
                        if(!qualificationsList.isEmpty()){
                            for (WebElement webElement : qualificationsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty() && !s.contains("Le Groupe EXPLEO s’appuie sur 19000")){
                                    qualificationsStr.append("- ").append(webElement.getText());
                                }
                            }
                        }else {
                            qualificationsList = dataDiv.get(2).findElements(By.tagName("p"));
                            for (WebElement webElement : qualificationsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty() && !s.contains("Le Groupe EXPLEO s’appuie sur 19000")){
                                    qualificationsStr.append("- ").append(webElement.getText());
                                }
                            }
                        }
                    }

                    id_jobMissions.put(i, missionsStr);
                    id_jobQualifications.put(i, qualificationsStr);

                    /*
                     *  Printing Results For Debbuging purposes.
                     * */
                    System.out.println(jobsLinks.get(i));
                    System.out.println("Missions : "+missionsStr);
                    System.out.println("Qualifications : "+qualificationsStr);
                    System.out.println("=========================================================");
                    System.out.println();

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error processing job row " + i, e);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
