package collectjobs;

import dataorganize.DataToExcel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AltenJobCollector {

    CompaniesLinks companiesLinks = new CompaniesLinks();

    WebDriver driver = new EdgeDriver();

    private final HashMap<Integer, List<String>> id_jobInfo = new HashMap<>();
    private final HashMap<Integer, String> jobsLinks = new HashMap<>();
    private final HashMap<Integer, StringBuilder> id_jobQualifications = new HashMap<>();
    private final HashMap<Integer, StringBuilder> id_jobMissions = new HashMap<>();

    public HashMap<Integer, StringBuilder> getId_jobQualifications() {
        return id_jobQualifications;
    }

    public HashMap<Integer, StringBuilder> getId_jobMissions() {
        return id_jobMissions;
    }

    public HashMap<Integer, String> getJobsLinks() {
        return jobsLinks;
    }

    public HashMap<Integer, List<String>> getId_jobInfo() {
        return id_jobInfo;
    }

    public void setUpDriver(){
        String link = companiesLinks.altenLink;
        driver.get(link);
    }

    public void closeDriver(){
        driver.quit();
    }

    public void getJobsInformations(){

        int job_id = 0;

        List<String> domainsLinks = new ArrayList<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {

            /*
             * Here, we access to the Domains block and collect the links of the
             * existing domains
             */
            WebElement domainsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("expertise")
            ));

            List<WebElement> listDomainDivs = domainsSection.findElements(
                    By.cssSelector(
                            "div.container-text div.g-4.justify-content-center.row.row-cols-1.row-cols-md-3.row-cols-sm-2.wp-block-bootstrap-row div.col.wp-block-bootstrap-column a")
            );

            for(WebElement element : listDomainDivs){
                domainsLinks.add(element.getAttribute("href"));
            }
            System.out.println(domainsLinks);


            /*
            * Here, we iterate over the domains, each domain contains a list of jobs.
            * For each job we collect its data.
            * */

            for(String link : domainsLinks){
                driver.get(link);
                WebElement jobsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.container-md div.wp-block-bootstrap-tabs ul.nav.tabs__nav.d-none.d-md-flex")
                ));

                List<WebElement> listJobs = jobsSection.findElements(
                        By.tagName("a")
                );

                System.out.println("size : "+listJobs.size());
                for(WebElement job : listJobs){
                    String jobTitle = job.getText();
                    String jobLink = listJobs.getFirst().getAttribute("href");

                    List<String> infos = new ArrayList<>();
                    infos.add(jobTitle);
                    infos.add("MA");
                    infos.add("N/A");
                    infos.add("N/A");
                    infos.add("N/A");
                    id_jobInfo.put(job_id, infos);
                    jobsLinks.put(job_id, jobLink);

                    job_id++;
                }

                for(int i=0; i<jobsLinks.size(); i++){
                    String jobLink = jobsLinks.get(i);
                    driver.get(link);

                    WebElement missionsList = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("div.container-md div.wp-block-bootstrap-tabs div.tab-pane.fade.wp-block-bootstrap-tab-item.active.show ul")
                    ));

                    StringBuilder missions = new StringBuilder("'- ");
                    List<WebElement> list_li = missionsList.findElements(By.tagName("li"));
                    for(WebElement element : list_li){
                        String str = element.getText();
                        missions.append(str).append("- ");
                    }
                    id_jobMissions.put(i, missions);

                    /* Job qualification must be empty because there is no qualification section
                    *  in Alten website.
                    * */
                    StringBuilder qualifications = new StringBuilder("N/A");
                    id_jobQualifications.put(i,qualifications);
                }


            }
            System.out.println(id_jobInfo);
            System.out.println(jobsLinks);
            System.out.println(id_jobMissions);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        AltenJobCollector altenJobCollector = new AltenJobCollector();
        altenJobCollector.setUpDriver();
        altenJobCollector.getJobsInformations();
        altenJobCollector.closeDriver();

        DataToExcel dataToExcel = new DataToExcel();
        dataToExcel.createWorkbook();
        dataToExcel.applyBorderStyle();
        dataToExcel.applyHeaderStyle();
        dataToExcel.writeTableHeader();
        dataToExcel.writeData("Alten", altenJobCollector.getId_jobInfo(), altenJobCollector.getJobsLinks(), altenJobCollector.getId_jobMissions(), altenJobCollector.getId_jobQualifications());

        dataToExcel.saveWorkbook("C://Users//touhafi//Desktop//outputAlten.xlsx");
    }
}
