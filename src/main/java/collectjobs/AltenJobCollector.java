package collectjobs;

import dataorganize.DataToExcel;
import org.openqa.selenium.*;
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
            // Handle cookie disclaimer before accessing any elements
            dismissCookieDisclaimer();

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
//            System.out.println(domainsLinks);


            /*
            * Here, we iterate over the domains, each domain contains a list of jobs.
            * For each job we collect its data.
            * */

            for(String link : domainsLinks){
                driver.get(link);
                // Dismiss cookie disclaimer on each new page
                dismissCookieDisclaimer();

                WebElement jobsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.container-md div.wp-block-bootstrap-tabs ul.nav.tabs__nav.d-none.d-md-flex")
                ));

                List<WebElement> listJobs = jobsSection.findElements(
                        By.tagName("a")
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void dismissCookieDisclaimer() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement disclaimer = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("tarteaucitronDisclaimerAlert")
            ));

            // Find and click the accept button (adjust selector as needed)
            WebElement acceptButton = driver.findElement(By.cssSelector("button.tarteaucitronAllow"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptButton);

            // Wait for disclaimer to disappear
            shortWait.until(ExpectedConditions.invisibilityOf(disclaimer));
        } catch (TimeoutException e) {
            // Disclaimer didn't appear, continue silently
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
