package ru.netology.Diploma_JD.util;

import org.springframework.web.multipart.MultipartFile;
import ru.netology.Diploma_JD.error.ErrorInputData;
import ru.netology.Diploma_JD.repository.CloudFileRepository;
import ru.netology.Diploma_JD.repository.ErrorRepository;

import java.security.Principal;

public class RenameFileUtil {

    public static boolean isFileExistInDb(CloudFileRepository fileRepository, String filename, Principal principal) {
        return fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()).isPresent();
    }

    // проверка на дубликат. Для проверки через хеш нужно сначала загрузить новый файл (чтоб создать ему хеш-код), в данном случае сравнение идет до загрузки
    public static boolean isFilePossibleDuplicate(CloudFileRepository fileRepository, MultipartFile uploadFile,
                                                  String filename, Principal principal) {
        return uploadFile.getSize() ==
                fileRepository.findCloudFileByFilenameAndUsername_UserName(filename, principal.getName()).get().getSize(); //'isPresent()' check в предыдущем методе
    }

    public static String checkUniqueName(CloudFileRepository fileRepository, ErrorRepository errorRepository, MultipartFile uploadFile,
                                         String filename, Principal principal) throws RuntimeException {
        if (isFileExistInDb(fileRepository, filename, principal)) {
            if (!isFilePossibleDuplicate(fileRepository, uploadFile, filename, principal)) {

                String newFilename = renameFile(filename);
                return checkUniqueName(fileRepository, errorRepository, uploadFile, newFilename, principal);
            }
            throw new ErrorInputData("ErrorInputData: Возможно это дубликат: файл с таким именем, расширением и размером уже существует", errorRepository.setNumber());
        }
        return filename;
    }

    public static String renameFile(String filename) {
        String newFilename = null;
        String[] splitByDot = filename.split("\\."); // разделитель точка
        String[] splitByOpeningBracket = splitByDot[0].split(" \\("); // разделитель пробел и скобка

        if (splitByOpeningBracket.length > 1) { // если пробел с открывающей скобкой есть, то длина массива больше 1
            String s = splitByOpeningBracket[splitByOpeningBracket.length - 1]; // берем последний эл-т массива
            if ((')') == (s.charAt(s.length() - 1))) { // проверяю, есть ли закрывающая скобка в конце
                String possibleNumber = s.substring(0, s.length() - 1); // если есть, то вынимаю содержимое; начальный индекс вкл, конечный индекс - не включая.
                if (stringIsNumber(possibleNumber)) {
                    int number = Integer.parseInt(possibleNumber);
                    number++;

                    if (splitByOpeningBracket.length > 2) { // маловероятная ситуация, но возможная
                        for (int i = 0; i < splitByOpeningBracket.length - 1; i++) {
                            newFilename = newFilename == null ? (splitByOpeningBracket[i] + " (") : (newFilename + splitByOpeningBracket[i] + " (");
                        }
                        return newFilename + number + ")." + splitByDot[1];
                    }

                    newFilename = splitByOpeningBracket[0] + " (" + number + ")." + splitByDot[1];
                    return newFilename;
                }
            }
        }
        newFilename = splitByDot[0] + " (1)." + splitByDot[1];
        return newFilename;
    }

    public static boolean stringIsNumber(String possibleNumber) {
        for (int i = 0; i < possibleNumber.length(); i++) { // на тот случай, если число двузначное или больше
            char c = possibleNumber.charAt(i);
            if (c != '1' && c != '2' && c != '3' && c != '4' && c != '5' && c != '6' && c != '7' && c != '8' &&
                    c != '9' && c != '0') {
                return false;
            }
        }
        return true;
    }
}