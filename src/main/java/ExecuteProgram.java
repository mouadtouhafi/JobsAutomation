import collectjobs.AltenJobCollector;
import collectjobs.ExpleoJobCollector;
import dataorganize.DataToExcel;

public class ExecuteProgram {
    public static void main(String[] args) {

        ExpleoJobCollector expleoJobCollector = new ExpleoJobCollector();
        expleoJobCollector.setUpDriver();
        expleoJobCollector.getJobsInformations();
        expleoJobCollector.getJobsRequirements();
        expleoJobCollector.closeDriver();

        System.out.println(" Printing the final Data : ");
        System.out.println(expleoJobCollector.getId_jobInfo());
        System.out.println(expleoJobCollector.getJobsLinks());
        System.out.println(expleoJobCollector.getId_jobMissions());
        System.out.println(expleoJobCollector.getId_jobQualifications());

        AltenJobCollector altenJobCollector = new AltenJobCollector();
        altenJobCollector.setUpDriver();
        altenJobCollector.getJobsInformations();
        altenJobCollector.closeDriver();

        System.out.println(" Printing the final Data : ");
        System.out.println(altenJobCollector.getId_jobInfo());
        System.out.println(altenJobCollector.getJobsLinks());
        System.out.println(altenJobCollector.getId_jobMissions());
        System.out.println(altenJobCollector.getId_jobQualifications());


        DataToExcel dataToExcel = new DataToExcel();
        dataToExcel.createWorkbook();
        dataToExcel.applyBorderStyle();
        dataToExcel.applyHeaderStyle();
        dataToExcel.writeTableHeader();
        dataToExcel.writeData("Expleo", expleoJobCollector.getId_jobInfo(), expleoJobCollector.getJobsLinks(), expleoJobCollector.getId_jobMissions(), expleoJobCollector.getId_jobQualifications());
        dataToExcel.writeData("Alten", altenJobCollector.getId_jobInfo(), altenJobCollector.getJobsLinks(), altenJobCollector.getId_jobMissions(), altenJobCollector.getId_jobQualifications());


        dataToExcel.saveWorkbook("C://Users//touhafi//Desktop//output.xlsx");

    }
}
