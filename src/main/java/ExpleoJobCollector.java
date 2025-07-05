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

    private HashMap<Integer, List<String>> id_jobInfo = new HashMap<>();
    private HashMap<Integer, StringBuilder> id_jobQualifications = new HashMap<>();
    private HashMap<Integer, StringBuilder> id_jobMissions = new HashMap<>();

    private final HashMap<Integer, String> jobsLinks = new HashMap<>();

    public HashMap<Integer, StringBuilder> getId_jobMissions() {
        return id_jobMissions;
    }

    public void setId_jobMissions(HashMap<Integer, StringBuilder> id_jobMissions) {
        this.id_jobMissions = id_jobMissions;
    }

    public HashMap<Integer, StringBuilder> getId_jobQualifications() {
        return id_jobQualifications;
    }

    public void setId_jobQualifications(HashMap<Integer, StringBuilder> id_jobQualifications) {
        this.id_jobQualifications = id_jobQualifications;
    }

    WebDriver driver = new EdgeDriver();



    public HashMap<Integer, List<String>> getId_jobInfo() {
        return id_jobInfo;
    }

    public void setId_jobInfo(HashMap<Integer, List<String>> id_jobInfo) {
        this.id_jobInfo = id_jobInfo;
    }

    void setUpDriver(){
        String link = companiesLinks.expleoLink;
        driver.get(link);
    }

    void closeDriver(){
        driver.quit();
    }

    void getJobsInformations(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            /*
             * Here we wait for the 5th iframe (index 4) to be available and automatically switch into it
             * This 4th iframe contains the dynamically loaded job listings.
             */
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(4));
            WebElement jobTable;
            jobTable = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("div.container-fluid.iCIMS_JobsTable")
                    )
            );
            List<WebElement> rows = jobTable.findElements(By.className("row"));

            for (int i=0; i<rows.size(); i++) {
                //System.out.println("Row " + (i + 1) + ": " + rows.get(i).getText());
                WebElement link = rows.get(i).findElement(By.cssSelector("div.col-xs-12.title a.iCIMS_Anchor"));
                WebElement job = rows.get(i).findElement(By.cssSelector("div.col-xs-12.title a.iCIMS_Anchor h3"));

                String jobLink = link.getAttribute("href");
                String jobTitle = job.getText();


                /*
                 * The four DIVs inside the element ".col-xs-12 additionalFields" have a repetitive structure,
                 * for that we used the term ":nth-of-type(4)" which indicates which block number we want to get.
                 * */
                String location = rows.get(i).findElement(By.cssSelector("div.col-xs-12.additionalFields > dl > div.iCIMS_JobHeaderTag:nth-of-type(1) dd.iCIMS_JobHeaderData span")).getText();
                String contractType = rows.get(i).findElement(By.cssSelector("div.col-xs-12.additionalFields > dl > div.iCIMS_JobHeaderTag:nth-of-type(3) dd.iCIMS_JobHeaderData span")).getText();
                String workMode = rows.get(i).findElement(By.cssSelector("div.col-xs-12.additionalFields > dl > div.iCIMS_JobHeaderTag:nth-of-type(4) dd.iCIMS_JobHeaderData span")).getText();

                /* Here we access the job link to collect the job requirements */

                List<String> infos = new ArrayList<>();
                infos.add(jobTitle);
                infos.add(location);
                infos.add(contractType);
                infos.add(workMode);

                id_jobInfo.put(i, infos);
                jobsLinks.put(i, jobLink);



            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        }
    }

    void getJobsRequirements(){
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
                            if(!s.isEmpty()){
                                missionsStr.append("- ").append(webElement.getText());
                            }
                        }
                    }else{
                        missionsList = dataDiv.get(1).findElements(By.tagName("div"));
                        if(!missionsList.isEmpty()){
                            for (WebElement webElement : missionsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty()){
                                    missionsStr.append("- ").append(webElement.getText());
                                }
                            }
                        }else {
                            missionsList = dataDiv.get(1).findElements(By.tagName("p"));
                            for (WebElement webElement : missionsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty()){
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
                            if(!s.isEmpty()){
                                qualificationsStr.append("- ").append(webElement.getText());
                            }
                        }
                    }else{
                        qualificationsList = dataDiv.get(2).findElements(By.tagName("div"));
                        if(!qualificationsList.isEmpty()){
                            for (WebElement webElement : qualificationsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty()){
                                    qualificationsStr.append("- ").append(webElement.getText());
                                }
                            }
                        }else {
                            qualificationsList = dataDiv.get(2).findElements(By.tagName("p"));
                            for (WebElement webElement : qualificationsList) {
                                String s = webElement.getText();
                                if(!s.isEmpty()){
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


    public static void main(String[] args) {

        ExpleoJobCollector expleoJobCollector = new ExpleoJobCollector();
        expleoJobCollector.setUpDriver();
        expleoJobCollector.getJobsInformations();
        expleoJobCollector.getJobsRequirements();
        expleoJobCollector.closeDriver();

        System.out.println(" Printing the final Data : ");
        System.out.println(expleoJobCollector.id_jobInfo);
        System.out.println(expleoJobCollector.jobsLinks);
        System.out.println(expleoJobCollector.id_jobMissions);
        System.out.println(expleoJobCollector.id_jobQualifications);

    }


}
