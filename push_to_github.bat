@echo off
chcp 65001 > nul
title SmartEdu - GitHub ga Yuklash
cls
echo ========================================================
echo        SmartEdu Loyihasini GitHub ga Yuklash
echo ========================================================
echo.
set /p repo="GitHub repository havolasini kiriting (masalan: https://github.com/username/SmartEdu.git): "
if "%repo%"=="" (
    echo Xatolik: Havola kiritilmadi!
    pause
    exit /b
)
echo.
echo GitHub ga ulanmoqda va fayllar yuklanmoqda...
git remote remove origin 2>nul
git remote add origin %repo%
git branch -M main
git push -u origin main
echo.
echo ========================================================
echo Muvaffaqiyatli yuklandi!
echo ========================================================
pause
